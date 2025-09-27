package com.org.bgv.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;
    
    private String first_name;
    private String last_name;
    private String nationality;


    @Column(name = "gender")
    private String gender;
    
    @Column(name = "dob", updatable = false)
    private LocalDate date_of_birth;
    
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;
    
    @Column(name = "marital_status", updatable = false)
    private String marital_status;
    
    @Column(name = "email_address", updatable = false)
    private String email_address;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "work_experience")
    private Boolean has_work_experience;
    
    @Column(name = "verification_status")
    private String verificationStatus;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
