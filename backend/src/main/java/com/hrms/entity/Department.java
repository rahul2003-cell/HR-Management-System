package com.hrms.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
@Entity @Table(name="departments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Department {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true) private String name;
    private String description;
    private String headName;
    @Builder.Default private boolean active = true;
    @OneToMany(mappedBy="department",fetch=FetchType.LAZY)
    @Builder.Default private List<Employee> employees = new ArrayList<>();
}
