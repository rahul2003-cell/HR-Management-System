package com.hrms.config;
import com.hrms.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.*;
@Configuration @EnableWebSecurity @EnableMethodSecurity @RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final UserDetailsService uds;
    @Value("${app.cors.allowed-origins}") private String allowedOrigins;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(c->c.configurationSource(corsSource()))
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a->a
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/admin/**").hasAnyRole("ADMIN","HR")
                .anyRequest().authenticated())
            .authenticationProvider(authProvider())
            .addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class)
            .headers(h->h.frameOptions(f->f.disable()));
        return http.build();
    }
    @Bean CorsConfigurationSource corsSource() {
        var cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        cfg.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        var src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**",cfg);
        return src;
    }
    @Bean AuthenticationProvider authProvider() {
        var p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds); p.setPasswordEncoder(passwordEncoder()); return p;
    }
    @Bean AuthenticationManager authManager(AuthenticationConfiguration c) throws Exception { return c.getAuthenticationManager(); }
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}