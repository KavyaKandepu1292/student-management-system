package com.vit.sms.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vit.sms.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    Optional<Student> findByEmail(String email);

    @Query("""
           select s from Student s
           where (:department is null or lower(s.department) = lower(:department))
           and (:keyword is null or lower(s.firstName) like lower(concat('%', :keyword, '%'))
                or lower(s.lastName) like lower(concat('%', :keyword, '%'))
                or lower(s.email) like lower(concat('%', :keyword, '%')))
           """)
    Page<Student> search(@Param("keyword") String keyword,
                          @Param("department") String department,
                          Pageable pageable);

    @Query("select avg(s.gpa) from Student s")
    Double findAverageGpa();

    @Query("select s.department, count(s) from Student s group by s.department")
    java.util.List<Object[]> countByDepartment();
}
