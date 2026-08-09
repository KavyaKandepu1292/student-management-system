package com.vit.sms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vit.sms.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findByCourseId(Long courseId);
    List<Attendance> findByStudentIdAndCourseId(Long studentId, Long courseId);

    long countByStudentIdAndStatus(Long studentId, Attendance.AttendanceStatus status);
    long countByStudentId(Long studentId);

    @org.springframework.data.jpa.repository.Query(
        "select a.status, count(a) from Attendance a group by a.status")
    List<Object[]> countGroupedByStatus();
}
