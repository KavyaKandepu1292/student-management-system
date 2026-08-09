package com.vit.sms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vit.sms.dto.StudentDto;
import com.vit.sms.entity.Student;
import com.vit.sms.exception.DuplicateResourceException;
import com.vit.sms.exception.ResourceNotFoundException;
import com.vit.sms.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Page<StudentDto> search(String keyword, String department, Pageable pageable) {
        return studentRepository.search(
                (keyword == null || keyword.isBlank()) ? null : keyword,
                (department == null || department.isBlank()) ? null : department,
                pageable
        ).map(this::toDto);
    }

    public StudentDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public StudentDto create(StudentDto dto) {
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("A student with this email already exists");
        }
        Student saved = studentRepository.save(Student.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .department(dto.getDepartment())
                .gpa(dto.getGpa())
                .build());
        return toDto(saved);
    }

    public StudentDto update(Long id, StudentDto dto) {
        Student student = findEntity(id);
        if (!student.getEmail().equalsIgnoreCase(dto.getEmail())
                && studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("A student with this email already exists");
        }
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setDepartment(dto.getDepartment());
        student.setGpa(dto.getGpa());
        return toDto(studentRepository.save(student));
    }

    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    private Student findEntity(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    private StudentDto toDto(Student s) {
        return StudentDto.builder()
                .id(s.getId())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .email(s.getEmail())
                .department(s.getDepartment())
                .gpa(s.getGpa())
                .build();
    }
}
