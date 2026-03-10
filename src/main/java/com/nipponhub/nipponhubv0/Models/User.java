package com.nipponhub.nipponhubv0.Models;

import java.time.LocalDateTime;

import com.nipponhub.nipponhubv0.Models.Enum.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "app_user")
@Data
public class User {
    @Id
    private String id;
    @Column(unique = true, nullable = false)
    private String email;

    private String phone;
    private String passwordHash;
    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    private boolean isActive = true;
    private LocalDateTime createdAt = LocalDateTime.now();
}
