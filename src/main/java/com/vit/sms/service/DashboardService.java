package com.vit.sms.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.vit.sms.dto.DashboardStatsDto;
import com.vit.sms.repository.AttendanceRepository;
import com.vit.sms.repository.CourseRepository;
import com.vit.sms.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final AttendanceRepository attendanceRepository;

    public DashboardStatsDto getStats() {
        long totalStudents = studentRepository.count();
        long totalCourses = courseRepository.count();
        Double avgGpa = studentRepository.findAverageGpa();

        Map<String, Long> byDepartment = new LinkedHashMap<>();
        for (Object[] row : studentRepository.countByDepartment()) {
            byDepartment.put((String) row[0], (Long) row[1]);
        }

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (Object[] row : attendanceRepository.countGroupedByStatus()) {
            byStatus.put(row[0].toString(), (Long) row[1]);
        }

        return DashboardStatsDto.builder()
                .totalStudents(totalStudents)
                .totalCourses(totalCourses)
                .averageGpa(avgGpa == null ? 0.0 : Math.round(avgGpa * 100) / 100.0)
                .studentsByDepartment(byDepartment)
                .attendanceByStatus(byStatus)
                .build();
    }
}
