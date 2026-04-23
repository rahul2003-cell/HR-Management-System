package com.hrms.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
public class DepartmentDto {
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id; private String name; private String description;
        private String headName; private boolean active; private long employeeCount;
    }
    @Data public static class Request {
        @NotBlank private String name; private String description; private String headName;
    }
}