package com.hrms.entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name="payroll") @EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payroll {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="employee_id") private Employee employee;
    private Integer month;
    private Integer year;
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal allowances;
    private BigDecimal grossSalary;
    private BigDecimal pf;
    private BigDecimal tax;
    private BigDecimal otherDeductions;
    private BigDecimal totalDeductions;
    private BigDecimal netSalary;
    private Integer presentDays;
    private Integer absentDays;
    private Integer leaveDays;
    @Enumerated(EnumType.STRING) @Builder.Default private PayrollStatus status = PayrollStatus.GENERATED;
    private LocalDateTime paidAt;
    @CreatedDate private LocalDateTime createdAt;
    public enum PayrollStatus { GENERATED, PAID, CANCELLED }
}
