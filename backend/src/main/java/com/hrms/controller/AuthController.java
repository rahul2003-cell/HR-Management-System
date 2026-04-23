package com.hrms.controller;
import com.hrms.dto.*;
import com.hrms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/auth") @RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> login(@Valid @RequestBody AuthDto.LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Login successful", authService.login(req)));
    }
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthDto.UserInfo>> me(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.ok(authService.me(ud.getUsername())));
    }
}