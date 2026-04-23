package com.hrms.dto;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
public class DashboardDto {
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Stats {
        private long totalEmployees; private long activeEmployees;
        private long presentToday; private long pendingLeaves;
        private BigDecimal monthlyPayroll; private BigDecimal totalPaidSalary;
        private long totalDepartments; private Map<String,Long> byDepartment;
        private Map<String,Long> byStatus; private List<RecentActivity> recentJoinees;
    }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RecentActivity {
        private String name; private String designation; private String department;
        private String employeeId; private String joinDate;
    }
}