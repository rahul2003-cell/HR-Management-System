package com.hrms.entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
@Entity @Table(name="users") @EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true) private String email;
    @Column(nullable=false) private String password;
    @Enumerated(EnumType.STRING) @Builder.Default private Role role = Role.EMPLOYEE;
    @OneToOne(mappedBy="user",fetch=FetchType.LAZY) private Employee employee;
    @Builder.Default private boolean enabled = true;
    @CreatedDate private LocalDateTime createdAt;
    @LastModifiedDate private LocalDateTime updatedAt;
    public enum Role { ADMIN, HR, EMPLOYEE }
}
