package com.hrms.entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity @Table(name="employees") @EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(unique=true,nullable=false) private String employeeId;
    @Column(nullable=false) private String firstName;
    @Column(nullable=false) private String lastName;
    @Column(unique=true,nullable=false) private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private LocalDate dateOfBirth;
    private LocalDate joiningDate;
    private String designation;
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal allowances;
    private BigDecimal deductions;
    private String profileImageUrl;
    private String panNumber;
    private String aadharNumber;
    private String bankAccount;
    private String bankName;
    private String ifscCode;
    @Enumerated(EnumType.STRING) @Builder.Default private Gender gender = Gender.OTHER;
    @Enumerated(EnumType.STRING) @Builder.Default private EmploymentType employmentType = EmploymentType.FULL_TIME;
    @Enumerated(EnumType.STRING) @Builder.Default private Status status = Status.ACTIVE;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="department_id") private Department department;
    @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id") private User user;
    @CreatedDate private LocalDateTime createdAt;
    @LastModifiedDate private LocalDateTime updatedAt;
    public String getFullName(){ return firstName + " " + lastName; }
    public BigDecimal getGrossSalary(){
        BigDecimal g = basicSalary != null ? basicSalary : BigDecimal.ZERO;
        if(hra!=null) g=g.add(hra);
        if(allowances!=null) g=g.add(allowances);
        return g;
    }
    public BigDecimal getNetSalary(){
        BigDecimal net = getGrossSalary();
        if(deductions!=null) net=net.subtract(deductions);
        return net;
    }
    public enum Gender { MALE, FEMALE, OTHER }
    public enum EmploymentType { FULL_TIME, PART_TIME, CONTRACT, INTERN }
    public enum Status { ACTIVE, INACTIVE, ON_LEAVE, TERMINATED }
}
