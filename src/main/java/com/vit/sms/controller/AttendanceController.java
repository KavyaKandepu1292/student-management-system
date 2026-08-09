package com.vit.sms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vit.sms.dto.AttendanceDto;
import com.vit.sms.service.AttendanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Attendance tracking per student/course")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get attendance records for a student")
    public ResponseEntity<List<AttendanceDto>> byStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getByStudent(studentId));
    }

    @GetMapping("/student/{studentId}/percentage")
    @Operation(summary = "Get attendance percentage for a student")
    public ResponseEntity<Map<String, Object>> percentage(@PathVariable Long studentId) {
        return ResponseEntity.ok(Map.of(
                "studentId", studentId,
                "attendancePercentage", attendanceService.attendancePercentageForStudent(studentId)));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get attendance records for a course")
    public ResponseEntity<List<AttendanceDto>> byCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(attendanceService.getByCourse(courseId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Record attendance (ADMIN only)")
    public ResponseEntity<AttendanceDto> record(@Valid @RequestBody AttendanceDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.record(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an attendance record (ADMIN only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attendanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
