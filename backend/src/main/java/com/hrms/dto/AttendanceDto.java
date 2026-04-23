package com.hrms.dto;
import com.hrms.entity.Attendance;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
public class AttendanceDto {
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id; private Long employeeId; private String employeeName;
        private String employeeCode; private LocalDate attendanceDate;
        private LocalTime checkIn; private LocalTime checkOut;
        private Attendance.AttendanceStatus status; private String remarks;
        private Double hoursWorked; private LocalDateTime createdAt;
    }
    @Data public static class MarkRequest {
        private Long employeeId; private LocalDate attendanceDate;
        private LocalTime checkIn; private LocalTime checkOut;
        private Attendance.AttendanceStatus status; private String remarks;
    }
    @Data public static class BulkMarkRequest {
        private LocalDate date; private java.util.List<MarkRequest> records;
    }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Summary {
        private int totalDays; private long present; private long absent;
        private long halfDay; private long late; private long onLeave; private long wfh;
        private double attendancePercent;
    }
}