package com.hrms.repository;

import com.hrms.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndAttendanceDate(Long empId, LocalDate date);

    List<Attendance> findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
            Long empId, LocalDate from, LocalDate to);

    List<Attendance> findByAttendanceDateOrderByEmployeeFirstNameAsc(LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.id = :empId " +
           "AND a.attendanceDate BETWEEN :from AND :to AND a.status = 'PRESENT'")
    long countPresentDays(@Param("empId") Long empId,
                          @Param("from") LocalDate from,
                          @Param("to") LocalDate to);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.id = :empId " +
           "AND a.attendanceDate BETWEEN :from AND :to AND a.status = 'ABSENT'")
    long countAbsentDays(@Param("empId") Long empId,
                         @Param("from") LocalDate from,
                         @Param("to") LocalDate to);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.attendanceDate = :date AND a.status = 'PRESENT'")
    long countPresentToday(@Param("date") LocalDate date);
}