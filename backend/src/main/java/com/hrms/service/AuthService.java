package com.hrms.service;

import com.hrms.dto.AuthDto;
import com.hrms.entity.User;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.repository.UserRepository;
import com.hrms.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final JwtUtils jwt;
    private final AuthenticationManager authManager;
    private final UserDetailsService uds;

    public AuthDto.AuthResponse login(AuthDto.LoginRequest req) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        // Explicitly typed as com.hrms.entity.User — no ambiguity
        User user = userRepo.findByEmail(req.getEmail()).orElseThrow();
        UserDetails ud = uds.loadUserByUsername(user.getEmail());
        String token = jwt.generate(ud);

        com.hrms.entity.Employee emp = user.getEmployee();
        return AuthDto.AuthResponse.builder()
            .accessToken(token)
            .tokenType("Bearer")
            .user(AuthDto.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .employeeId(emp != null ? emp.getId() : null)
                .name(emp != null ? emp.getFullName() : user.getEmail().split("@")[0])
                .profileImageUrl(emp != null ? emp.getProfileImageUrl() : null)
                .build())
            .build();
    }

    public AuthDto.UserInfo me(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        com.hrms.entity.Employee emp = user.getEmployee();
        return AuthDto.UserInfo.builder()
            .id(user.getId())
            .email(user.getEmail())
            .role(user.getRole())
            .employeeId(emp != null ? emp.getId() : null)
            .name(emp != null ? emp.getFullName() : user.getEmail().split("@")[0])
            .profileImageUrl(emp != null ? emp.getProfileImageUrl() : null)
            .build();
    }
}