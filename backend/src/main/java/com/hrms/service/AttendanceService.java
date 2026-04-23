package com.hrms.service;

import com.hrms.dto.AttendanceDto;
import com.hrms.entity.Attendance;
import com.hrms.entity.Employee;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.repository.AttendanceRepository;
import com.hrms.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository repo;
    private final EmployeeRepository empRepo;

    public List<AttendanceDto.Response> getByDate(LocalDate date) {
        return repo.findByAttendanceDateOrderByEmployeeFirstNameAsc(date)
                   .stream().map(this::map).toList();
    }

    public List<AttendanceDto.Response> getByEmployeeAndRange(Long empId, LocalDate from, LocalDate to) {
        return repo.findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(empId, from, to)
                   .stream().map(this::map).toList();
    }

    @Transactional
    public AttendanceDto.Response mark(AttendanceDto.MarkRequest req) {
        Employee emp = empRepo.findById(req.getEmployeeId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee", req.getEmployeeId()));

        LocalDate date = req.getAttendanceDate() != null ? req.getAttendanceDate() : LocalDate.now();
        Attendance att = repo.findByEmployeeIdAndAttendanceDate(emp.getId(), date)
            .orElse(Attendance.builder().employee(emp).attendanceDate(date).build());

        att.setStatus(req.getStatus() != null ? req.getStatus() : Attendance.AttendanceStatus.PRESENT);
        att.setCheckIn(req.getCheckIn());
        att.setCheckOut(req.getCheckOut());
        att.setRemarks(req.getRemarks());

        if (req.getCheckIn() != null && req.getCheckOut() != null) {
            att.setHoursWorked((double) ChronoUnit.MINUTES.between(req.getCheckIn(), req.getCheckOut()) / 60.0);
        }
        return map(repo.save(att));
    }

    public AttendanceDto.Summary getSummary(Long empId, LocalDate from, LocalDate to) {
        List<Attendance> list = repo.findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(empId, from, to);

        long present = list.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT).count();
        long absent  = list.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.ABSENT).count();
        long half    = list.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.HALF_DAY).count();
        long late    = list.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.LATE).count();
        long leave   = list.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.ON_LEAVE).count();
        long wfh     = list.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.WORK_FROM_HOME).count();

        int total = list.size();
        double pct = total > 0 ? (double)(present + half + late + wfh) / total * 100 : 0;

        return AttendanceDto.Summary.builder()
            .totalDays(total).present(present).absent(absent)
            .halfDay(half).late(late).onLeave(leave).wfh(wfh)
            .attendancePercent(Math.round(pct * 10.0) / 10.0)
            .build();
    }

    private AttendanceDto.Response map(Attendance a) {
        Employee e = a.getEmployee();
        return AttendanceDto.Response.builder()
            .id(a.getId()).employeeId(e.getId())
            .employeeName(e.getFullName()).employeeCode(e.getEmployeeId())
            .attendanceDate(a.getAttendanceDate())
            .checkIn(a.getCheckIn()).checkOut(a.getCheckOut())
            .status(a.getStatus()).remarks(a.getRemarks())
            .hoursWorked(a.getHoursWorked()).createdAt(a.getCreatedAt())
            .build();
    }
}