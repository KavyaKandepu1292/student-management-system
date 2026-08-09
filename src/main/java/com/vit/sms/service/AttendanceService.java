package com.vit.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vit.sms.dto.AttendanceDto;
import com.vit.sms.entity.Attendance;
import com.vit.sms.entity.Course;
import com.vit.sms.entity.Student;
import com.vit.sms.exception.ResourceNotFoundException;
import com.vit.sms.repository.AttendanceRepository;
import com.vit.sms.repository.CourseRepository;
import com.vit.sms.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public List<AttendanceDto> getByStudent(Long studentId) {
        return attendanceRepository.findByStudentId(studentId).stream().map(this::toDto).toList();
    }

    public List<AttendanceDto> getByCourse(Long courseId) {
        return attendanceRepository.findByCourseId(courseId).stream().map(this::toDto).toList();
    }

    public AttendanceDto record(AttendanceDto dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + dto.getStudentId()));
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + dto.getCourseId()));

        Attendance saved = attendanceRepository.save(Attendance.builder()
                .student(student)
                .course(course)
                .date(dto.getDate())
                .status(dto.getStatus())
                .build());
        return toDto(saved);
    }

    public void delete(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance record not found with id: " + id);
        }
        attendanceRepository.deleteById(id);
    }

    public double attendancePercentageForStudent(Long studentId) {
        long total = attendanceRepository.countByStudentId(studentId);
        if (total == 0) {
			return 0.0;
		}
        long present = attendanceRepository.countByStudentIdAndStatus(studentId, Attendance.AttendanceStatus.PRESENT);
        return Math.round((present * 10000.0 / total)) / 100.0;
    }

    private AttendanceDto toDto(Attendance a) {
        return AttendanceDto.builder()
                .id(a.getId())
                .studentId(a.getStudent().getId())
                .courseId(a.getCourse().getId())
                .date(a.getDate())
                .status(a.getStatus())
                .build();
    }
}
