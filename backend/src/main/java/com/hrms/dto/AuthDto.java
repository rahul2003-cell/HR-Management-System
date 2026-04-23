package com.hrms.dto;
import com.hrms.entity.User;
import jakarta.validation.constraints.*;
import lombok.*;
public class AuthDto {
    @Data public static class LoginRequest {
        @NotBlank @Email private String email;
        @NotBlank private String password;
    }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AuthResponse {
        private String accessToken;
        private String tokenType;
        private UserInfo user;
    }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserInfo {
        private Long id; private String email; private User.Role role;
        private Long employeeId; private String name; private String profileImageUrl;
    }
}