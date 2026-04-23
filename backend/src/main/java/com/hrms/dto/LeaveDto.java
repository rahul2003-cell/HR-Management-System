package com.hrms.dto;
import com.hrms.entity.Leave;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
public class LeaveDto {
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id; private Long employeeId; private String employeeName;
        private String employeeCode; private Leave.LeaveType leaveType;
        private LocalDate fromDate; private LocalDate toDate; private Integer totalDays;
        private String reason; private Leave.LeaveStatus status; private String adminRemarks;
        private LocalDateTime createdAt;
    }
    @Data public static class Request {
        @NotNull private Leave.LeaveType leaveType;
        @NotNull private LocalDate fromDate; @NotNull private LocalDate toDate;
        private String reason;
    }
    @Data public static class ActionRequest {
        @NotNull private Leave.LeaveStatus status; private String adminRemarks;
    }
}