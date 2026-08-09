package com.vit.sms.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardStatsDto {
    private long totalStudents;
    private long totalCourses;
    private double averageGpa;
    private Map<String, Long> studentsByDepartment;
    private Map<String, Long> attendanceByStatus;
}
