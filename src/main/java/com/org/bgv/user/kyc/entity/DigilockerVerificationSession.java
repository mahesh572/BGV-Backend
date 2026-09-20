package com.org.bgv.user.kyc.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.org.bgv.constants.DocumentType;
import com.org.bgv.user.kyc.emums.DigiLockerAccountStatus;
import com.org.bgv.user.kyc.emums.DigiLockerSessionStatus;
import com.org.bgv.user.kyc.emums.DigiLockerUserFlow;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "digilocker_verification_session")
public class DigilockerVerificationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @Column(nullable = false, unique = true)
    private String verificationId;

    private Long referenceId;

    private String digilockerId;

    @Enumerated(EnumType.STRING)
    private DigiLockerUserFlow userFlow;
    // SIGNIN
    // SIGNUP

    @Enumerated(EnumType.STRING)
    private DigiLockerAccountStatus accountStatus;
    // ACCOUNT_EXISTS
    // ACCOUNT_NOT_EXISTS

    @Enumerated(EnumType.STRING)
    private DigiLockerSessionStatus sessionStatus;
    // CREATED
    // PENDING
    // AUTHENTICATED
    // FAILED
    // EXPIRED

    private String redirectUrl;

    private String consentUrl;

    private LocalDateTime authenticatedAt;

    @Lob
    private String rawResponse;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
