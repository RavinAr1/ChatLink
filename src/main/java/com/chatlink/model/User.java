package com.chatlink.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String uniqueCode;

    private int connectionsCount = 0;

    // Email verification
    private boolean verified = false;

    @Column(unique = true)
    private String verificationToken;

    // Password reset
    private String resetToken;
    private Long resetTokenExpiry;
}
