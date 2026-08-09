package com.vit.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vit.sms.dto.CourseDto;
import com.vit.sms.entity.Course;
import com.vit.sms.exception.DuplicateResourceException;
import com.vit.sms.exception.ResourceNotFoundException;
import com.vit.sms.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<CourseDto> getAll() {
        return courseRepository.findAll().stream().map(this::toDto).toList();
    }

    public CourseDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public CourseDto create(CourseDto dto) {
        if (courseRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("A course with this code already exists");
        }
        Course saved = courseRepository.save(Course.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .department(dto.getDepartment())
                .credits(dto.getCredits())
                .build());
        return toDto(saved);
    }

    public CourseDto update(Long id, CourseDto dto) {
        Course course = findEntity(id);
        if (!course.getCode().equalsIgnoreCase(dto.getCode())
                && courseRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("A course with this code already exists");
        }
        course.setCode(dto.getCode());
        course.setName(dto.getName());
        course.setDepartment(dto.getDepartment());
        course.setCredits(dto.getCredits());
        return toDto(courseRepository.save(course));
    }

    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    private Course findEntity(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    private CourseDto toDto(Course c) {
        return CourseDto.builder()
                .id(c.getId())
                .code(c.getCode())
                .name(c.getName())
                .department(c.getDepartment())
                .credits(c.getCredits())
                .build();
    }
}
