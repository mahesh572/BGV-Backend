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
    
    @Column(name = "candidate_id", nullable = false, unique = true)
    private Long candidateId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
     
            
    @Column(name = "nationality")
    private String nationality;

    @Column(name = "gender")
    private String gender;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;
    
    @Column(name = "marital_status")
    private String maritalStatus;
    
    @Column(name = "email_address")
    private String emailAddress;

    

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
    
    @Column(name = "has_work_experience")
    private Boolean hasWorkExperience;
    
    @Column(name = "verification_status")
    private String verificationStatus = "pending";;
    
    @Column(name = "linkedin_url")
    private String linkedinUrl;
    
    @Column(name = "passport_number")
    private String passportNumber;
    
    @Column(name = "passport_expiry")
    private LocalDate passportExpiry;
    
    @Column(name = "verified")
    private Boolean verified = false;
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(name = "verified_by")
    private String verifiedBy;
    
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}