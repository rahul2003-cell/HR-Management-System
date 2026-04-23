package com.hrms.controller;

import com.hrms.dto.ApiResponse;
import com.hrms.dto.DashboardDto;
import com.hrms.entity.Employee;
import com.hrms.entity.Leave;
import com.hrms.repository.*;
import com.hrms.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class DashboardController {

    private final EmployeeRepository empRepo;
    private final AttendanceRepository attRepo;
    private final LeaveRepository leaveRepo;
    private final PayrollRepository payrollRepo;
    private final DepartmentRepository deptRepo;
    private final ExportService exportSvc;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardDto.Stats>> dashboard() {
        long total        = empRepo.count();
        long active       = empRepo.countByStatus(Employee.Status.ACTIVE);
        long present      = attRepo.countPresentToday(LocalDate.now());   // correct method name
        long pendingLeaves= leaveRepo.countByStatus(Leave.LeaveStatus.PENDING);

        var monthly   = payrollRepo.getMonthlyPayroll(LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        var totalPaid = payrollRepo.getTotalPaidSalary();
        long depts    = deptRepo.count();

        Map<String, Long> byDept = deptRepo.findAll().stream()
            .collect(Collectors.toMap(
                d -> d.getName(),
                d -> empRepo.countByDepartmentId(d.getId())
            ));

        Map<String, Long> byStatus = Map.of(
            "ACTIVE",   empRepo.countByStatus(Employee.Status.ACTIVE),
            "INACTIVE", empRepo.countByStatus(Employee.Status.INACTIVE),
            "ON_LEAVE", empRepo.countByStatus(Employee.Status.ON_LEAVE)
        );

        var recent = empRepo.findAll(PageRequest.of(0, 5, Sort.by("createdAt").descending()))
            .getContent().stream()
            .map(e -> DashboardDto.RecentActivity.builder()
                .name(e.getFullName())
                .designation(e.getDesignation())
                .department(e.getDepartment() != null ? e.getDepartment().getName() : "")
                .employeeId(e.getEmployeeId())
                .joinDate(e.getJoiningDate() != null ? e.getJoiningDate().toString() : "")
                .build())
            .toList();

        return ResponseEntity.ok(ApiResponse.ok(DashboardDto.Stats.builder()
            .totalEmployees(total).activeEmployees(active).presentToday(present)
            .pendingLeaves(pendingLeaves).monthlyPayroll(monthly).totalPaidSalary(totalPaid)
            .totalDepartments(depts).byDepartment(byDept).byStatus(byStatus)
            .recentJoinees(recent).build()));
    }

    @GetMapping("/export/employees")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<byte[]> exportEmployees() {
        byte[] data = exportSvc.exportEmployeesExcel();
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=employees.xlsx")
            .body(data);
    }
}