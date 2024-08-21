package com.cos.security1.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role; // ADMIN, USER, MANAGER
    private String provider; // 소셜로그인 구분
    private String providerId; // 소셜로그인 ID
    @CreationTimestamp
    private Timestamp createDate;

}
