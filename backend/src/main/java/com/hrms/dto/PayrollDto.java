package com.hrms.dto;
import com.hrms.entity.Payroll;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class PayrollDto {
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id; private Long employeeId; private String employeeName;
        private String employeeCode; private String designation; private String departmentName;
        private Integer month; private Integer year;
        private BigDecimal basicSalary; private BigDecimal hra; private BigDecimal allowances;
        private BigDecimal grossSalary; private BigDecimal pf; private BigDecimal tax;
        private BigDecimal otherDeductions; private BigDecimal totalDeductions; private BigDecimal netSalary;
        private Integer presentDays; private Integer absentDays; private Integer leaveDays;
        private Payroll.PayrollStatus status; private LocalDateTime paidAt; private LocalDateTime createdAt;
    }
    @Data public static class GenerateRequest {
        private Integer month; private Integer year;
        private java.util.List<Long> employeeIds;
    }
}