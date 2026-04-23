package com.hrms.entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity @Table(name="leaves") @EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Leave {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="employee_id") private Employee employee;
    @Enumerated(EnumType.STRING) private LeaveType leaveType;
    @Column(nullable=false) private LocalDate fromDate;
    @Column(nullable=false) private LocalDate toDate;
    private Integer totalDays;
    private String reason;
    @Enumerated(EnumType.STRING) @Builder.Default private LeaveStatus status = LeaveStatus.PENDING;
    private String adminRemarks;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="approved_by") private User approvedBy;
    private LocalDateTime approvedAt;
    @CreatedDate private LocalDateTime createdAt;
    public enum LeaveType { CASUAL, SICK, EARNED, MATERNITY, PATERNITY, UNPAID }
    public enum LeaveStatus { PENDING, APPROVED, REJECTED, CANCELLED }
}
