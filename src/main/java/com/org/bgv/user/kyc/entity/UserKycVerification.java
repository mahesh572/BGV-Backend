package com.org.bgv.user.kyc.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.org.bgv.constants.DocumentType;
import com.org.bgv.entity.User;
import com.org.bgv.user.enums.KycProvider;
import com.org.bgv.user.enums.KycVerificationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_kyc_verification")
@Getter
@Setter
public class UserKycVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;
    // PAN, AADHAAR, PASSPORT, DL

    @Column(length = 100)
    private String documentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycVerificationStatus status;

    @Enumerated(EnumType.STRING)
    private KycProvider provider;

    @Column(length = 200)
    private String providerReference;

    private LocalDateTime verifiedAt;

    private LocalDateTime expiresAt;

    @Column(length = 500)
    private String remarks;

    @Column(length = 2000)
    private String registeredName;

    @Column(length = 2000)
    private String rawResponse;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}