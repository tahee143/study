package com.cos.jwt.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String roles; // ADMIN, USER, MANAGER
    private String provider; // 소셜로그인 구분
    private String providerId; // 소셜로그인 ID
    @CreationTimestamp
    private Timestamp createDate;

    @Builder
    public User(String username, String password, String email, String roles, String provider, String providerId, Timestamp createDate) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.provider = provider;
        this.providerId = providerId;
        this.createDate = createDate;
    }

    public List<String> getRoleList() {
        if(this.roles.length() > 0) {
            return Arrays.asList(this.roles.split(","));
        }

        return new ArrayList<>();
    }
}
