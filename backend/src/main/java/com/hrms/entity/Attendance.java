package com.hrms.entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
@Entity @Table(name="attendance",uniqueConstraints=@UniqueConstraint(columnNames={"employee_id","attendance_date"}))
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attendance {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="employee_id") private Employee employee;
    @Column(nullable=false) private LocalDate attendanceDate;
    private LocalTime checkIn;
    private LocalTime checkOut;
    @Enumerated(EnumType.STRING) @Builder.Default private AttendanceStatus status = AttendanceStatus.PRESENT;
    private String remarks;
    private Double hoursWorked;
    @CreatedDate private LocalDateTime createdAt;
    public enum AttendanceStatus { PRESENT, ABSENT, HALF_DAY, LATE, ON_LEAVE, HOLIDAY, WORK_FROM_HOME }
}
