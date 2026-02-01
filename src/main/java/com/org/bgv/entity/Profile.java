package com.org.bgv.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "profiles",
    indexes = {
        @Index(name = "idx_profile_user", columnList = "user_id"),
        @Index(name = "idx_profile_pan_hash", columnList = "pan_hash"),
        @Index(name = "idx_profile_aadhaar_hash", columnList = "aadhaar_hash")
    }
)
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

    /**
     * Profile belongs to a global User (candidate login)
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /* =========================
       BASIC PERSONAL DETAILS
       ========================= */

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

    @Column(name = "marital_status")
    private String maritalStatus;

    /* =========================
       CONTACT DETAILS
       ========================= */

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "phone_verified")
    private Boolean phoneVerified;

    @Column(name = "phone_verified_at")
    private LocalDateTime phoneVerifiedAt;

    /* =========================
       IDENTITY (HASHED)
       ========================= */

    @Column(name = "pan_hash", length = 64)
    private String panHash;

    @Column(name = "aadhaar_hash", length = 64)
    private String aadhaarHash;

    @Column(name = "identity_verified")
    private Boolean identityVerified;

    @Column(name = "identity_verified_at")
    private LocalDateTime identityVerifiedAt;

    @Column(name = "identity_verified_by")
    private String identityVerifiedBy;

    /* =========================
       DOCUMENT INFO
       ========================= */

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "passport_expiry")
    private LocalDate passportExpiry;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    /* =========================
       CONSENT & LEGAL
       ========================= */

    @Column(name = "consent_provided")
    private Boolean consentProvided;

    @Column(name = "consent_provided_at")
    private LocalDateTime consentProvidedAt;

    @Column(name = "consent_source")
    private String consentSource;
    // SELF, EMPLOYER, UNIVERSITY, SYSTEM

    /* =========================
       SOURCE & AUDIT
       ========================= */

    @Column(name = "profile_source")
    private String profileSource;
    // SELF, EMPLOYER, UNIVERSITY

    @Column(name = "last_updated_source")
    private String lastUpdatedSource;
    // CANDIDATE, EMPLOYER, SYSTEM

    @Column(name = "status")
    private String status;
    // ACTIVE, INACTIVE

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    /* =========================
       LIFECYCLE HOOKS
       ========================= */

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = "ACTIVE";
        }
        if (this.phoneVerified == null) {
            this.phoneVerified = false;
        }
        if (this.identityVerified == null) {
            this.identityVerified = false;
        }
        if (this.consentProvided == null) {
            this.consentProvided = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
