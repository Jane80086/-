package com.cemenghui.course.common;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;
    @Column(name = "username", nullable = false, unique = true)
    protected String username;
    @Column(name = "password", nullable = false)
    protected String password;
    @Column(name = "email", nullable = false, unique = true)
    protected String email;
    @Column(name = "phone")
    protected String phone;
    @Column(name = "status")
    protected Integer status = 1;
    @Column(name = "created_time")
    protected LocalDateTime createdTime = LocalDateTime.now();
    @Column(name = "updated_time")
    protected LocalDateTime updatedTime = LocalDateTime.now();
} 