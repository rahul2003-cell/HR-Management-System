package com.hrms.dto;
import com.hrms.entity.Employee;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
public class EmployeeDto {
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id; private String employeeId; private String firstName; private String lastName;
        private String fullName; private String email; private String phone; private String address;
        private String city; private String state; private String pincode;
        private LocalDate dateOfBirth; private LocalDate joiningDate;
        private String designation; private BigDecimal basicSalary; private BigDecimal hra;
        private BigDecimal allowances; private BigDecimal deductions;
        private BigDecimal grossSalary; private BigDecimal netSalary;
        private String profileImageUrl; private String panNumber; private String aadharNumber;
        private String bankAccount; private String bankName; private String ifscCode;
        private Employee.Gender gender; private Employee.EmploymentType employmentType;
        private Employee.Status status; private Long departmentId; private String departmentName;
        private LocalDateTime createdAt;
    }
    @Data public static class Request {
        @NotBlank private String firstName; @NotBlank private String lastName;
        @NotBlank @Email private String email; private String phone; private String address;
        private String city; private String state; private String pincode;
        private LocalDate dateOfBirth; private LocalDate joiningDate;
        private String designation; @NotNull private BigDecimal basicSalary;
        private BigDecimal hra; private BigDecimal allowances; private BigDecimal deductions;
        private String profileImageUrl; private String panNumber; private String aadharNumber;
        private String bankAccount; private String bankName; private String ifscCode;
        private Employee.Gender gender; private Employee.EmploymentType employmentType;
        @NotNull private Long departmentId; private String password;
    }
    @Data public static class UpdateRequest {
        private String firstName; private String lastName; private String phone;
        private String address; private String city; private String state; private String pincode;
        private String designation; private BigDecimal basicSalary; private BigDecimal hra;
        private BigDecimal allowances; private BigDecimal deductions;
        private String profileImageUrl; private String panNumber; private String aadharNumber;
        private String bankAccount; private String bankName; private String ifscCode;
        private Employee.Gender gender; private Employee.EmploymentType employmentType;
        private Employee.Status status; private Long departmentId;
    }
}