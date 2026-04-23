package com.hrms;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication @EnableJpaAuditing @EnableAsync @EnableScheduling
public class HrmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(HrmsApplication.class, args);
        System.out.println("""
        ╔══════════════════════════════════════════╗
        ║   HRMS - Employee Management System      ║
        ║   API: http://localhost:8080/api          ║
        ║   H2:  http://localhost:8080/api/h2-console║
        ╚══════════════════════════════════════════╝
        """);
    }
}
