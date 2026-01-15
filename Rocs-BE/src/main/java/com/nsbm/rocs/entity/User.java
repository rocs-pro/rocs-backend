package com.nsbm.rocs.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User{

    // id BIGINT AUTO_INCREMENT PRIMARY KEY
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long userId;

    // username VARCHAR(100) UNIQUE NOT NULL
    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String userName;

    // password VARCHAR(255) NOT NULL
    @Column(name = "password", nullable = false, length = 255)
    private String password;  // hashed

    // full_name VARCHAR(255)
    @Column(name = "full_name", length = 255)
    private String fullName;

    // email VARCHAR(150)
    @Column(name = "email", length = 150)
    private String email;

    // status VARCHAR(20) DEFAULT 'ACTIVE'
    @Column(name = "status", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    private String status;

    // created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    private LocalDateTime createdAt;

    // add role column (enum)
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'USER'")
    private Role role;
}
