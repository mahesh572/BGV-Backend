package com.org.bgv.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {

    private Long profileId;
    private Long userId;

    /* =========================
       BASIC PERSONAL DETAILS
       ========================= */

    private String namePrefix;          // MR, MRS, MS, DR
    private String firstName;
    private String lastName;

    private String parentName;
    private String parentRelationship;  // FATHER, MOTHER, GUARDIAN

    private String nationality;
    private String gender;
    private LocalDate dateOfBirth;
    private String maritalStatus;
    
    private String profilePicUrl;

    /* =========================
       CONTACT DETAILS
       ========================= */

    private String phoneNumber;
    private Boolean phoneVerified;
    private LocalDateTime phoneVerifiedAt;

    private String linkedinUrl;

    /* =========================
       CONSENT & LEGAL
       ========================= */

    private Boolean consentProvided;
    private LocalDateTime consentProvidedAt;
    private String consentSource;

    /* =========================
       SOURCE & STATUS
       ========================= */

    private String profileSource;
    private String lastUpdatedSource;
    private String status;

    /* =========================
       AUDIT
       ========================= */

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
