package com.hrms.service;

import com.hrms.dto.PayrollDto;
import com.hrms.entity.Employee;
import com.hrms.entity.Payroll;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.repository.AttendanceRepository;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository repo;
    private final EmployeeRepository empRepo;
    private final AttendanceRepository attRepo;

    public List<PayrollDto.Response> getByEmployee(Long empId) {
        return repo.findByEmployeeIdOrderByYearDescMonthDesc(empId)
                   .stream().map(this::map).toList();
    }

    public List<PayrollDto.Response> getMonthly(int month, int year) {
        return repo.findByMonthAndYearOrderByEmployeeFirstNameAsc(month, year)
                   .stream().map(this::map).toList();
    }

    @Transactional
    public List<PayrollDto.Response> generate(PayrollDto.GenerateRequest req) {
        List<Employee> employees;
        if (req.getEmployeeIds() != null && !req.getEmployeeIds().isEmpty()) {
            employees = empRepo.findAllById(req.getEmployeeIds());
        } else {
            employees = empRepo.findAll().stream()
                .filter(e -> e.getStatus() == Employee.Status.ACTIVE)
                .toList();
        }
        return employees.stream()
            .map(emp -> generateForEmployee(emp, req.getMonth(), req.getYear()))
            .map(this::map)
            .toList();
    }

    private Payroll generateForEmployee(Employee emp, int month, int year) {
        // Return existing if already generated
        return repo.findByEmployeeIdAndMonthAndYear(emp.getId(), month, year)
            .orElseGet(() -> {
                LocalDate from = LocalDate.of(year, month, 1);
                LocalDate to   = from.withDayOfMonth(from.lengthOfMonth());

                // Use renamed methods: countPresentDays / countAbsentDays
                long present = attRepo.countPresentDays(emp.getId(), from, to);
                long absent  = attRepo.countAbsentDays(emp.getId(), from, to);

                BigDecimal basic  = nvl(emp.getBasicSalary());
                BigDecimal hra    = nvl(emp.getHra());
                BigDecimal allow  = nvl(emp.getAllowances());
                BigDecimal gross  = basic.add(hra).add(allow);

                BigDecimal pf     = basic.multiply(BigDecimal.valueOf(0.12)).setScale(2, RoundingMode.HALF_UP);
                BigDecimal tax    = gross.compareTo(BigDecimal.valueOf(50000)) > 0
                                    ? gross.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP)
                                    : BigDecimal.ZERO;
                BigDecimal otherDed = nvl(emp.getDeductions());
                BigDecimal totalDed = pf.add(tax).add(otherDed);
                BigDecimal net    = gross.subtract(totalDed);

                Payroll p = Payroll.builder()
                    .employee(emp).month(month).year(year)
                    .basicSalary(basic).hra(hra).allowances(allow).grossSalary(gross)
                    .pf(pf).tax(tax).otherDeductions(otherDed)
                    .totalDeductions(totalDed).netSalary(net)
                    .presentDays((int) present).absentDays((int) absent)
                    .leaveDays(from.lengthOfMonth() - (int) present - (int) absent)
                    .status(Payroll.PayrollStatus.GENERATED)
                    .build();
                return repo.save(p);
            });
    }

    @Transactional
    public PayrollDto.Response markPaid(Long id) {
        Payroll p = repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payroll", id));
        p.setStatus(Payroll.PayrollStatus.PAID);
        p.setPaidAt(LocalDateTime.now());
        return map(repo.save(p));
    }

    private BigDecimal nvl(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private PayrollDto.Response map(Payroll p) {
        Employee e = p.getEmployee();
        return PayrollDto.Response.builder()
            .id(p.getId()).employeeId(e.getId())
            .employeeName(e.getFullName()).employeeCode(e.getEmployeeId())
            .designation(e.getDesignation())
            .departmentName(e.getDepartment() != null ? e.getDepartment().getName() : null)
            .month(p.getMonth()).year(p.getYear())
            .basicSalary(p.getBasicSalary()).hra(p.getHra()).allowances(p.getAllowances())
            .grossSalary(p.getGrossSalary()).pf(p.getPf()).tax(p.getTax())
            .otherDeductions(p.getOtherDeductions()).totalDeductions(p.getTotalDeductions())
            .netSalary(p.getNetSalary())
            .presentDays(p.getPresentDays()).absentDays(p.getAbsentDays()).leaveDays(p.getLeaveDays())
            .status(p.getStatus()).paidAt(p.getPaidAt()).createdAt(p.getCreatedAt())
            .build();
    }
}