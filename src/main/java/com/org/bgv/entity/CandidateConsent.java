package com.org.bgv.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.org.bgv.candidate.entity.Candidate;

@Entity
@Table(
    name = "candidate_consent",
    indexes = {
        @Index(name = "idx_consent_candidate", columnList = "candidate_id"),
        @Index(name = "idx_consent_type", columnList = "consent_type"),
        @Index(name = "idx_consent_status", columnList = "status"),
        @Index(name = "idx_consent_policy", columnList = "policy_version")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateConsent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consent_id")
    private Long consentId;

    /* =======================
       Candidate Mapping
       ======================= */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    /* =======================
       Consent Type
       ======================= */
    @Enumerated(EnumType.STRING)
    @Column(name = "consent_type", nullable = false)
    private ConsentType consentType;

    /* =======================
       Signature (Canvas)
       ======================= */
    @Column(name = "signature_data", columnDefinition = "TEXT")
    private String signatureData; // JSON / base64 canvas data

    @Column(name = "signature_url")
    private String signatureUrl; // S3 URL

    @Column(name = "signature_s3_key")
    private String signatureS3Key;

    @Column(name = "signature_hash", length = 64)
    private String signatureHash; // SHA-256 hash for tamper detection

    /* =======================
       Uploaded Document
       ======================= */
    @Column(name = "document_url")
    private String documentUrl;

    @Column(name = "document_s3_key")
    private String documentS3Key;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    /* =======================
       Consent Metadata
       ======================= */
    @Column(name = "consent_source", nullable = false)
    private String consentSource; 
    // CANDIDATE, EMPLOYER, UNIVERSITY, SYSTEM

    @Column(name = "status", nullable = false)
    private String status; 
    // ACTIVE, REVOKED, EXPIRED

    @Column(name = "policy_version", nullable = false)
    private String policyVersion;

    @Column(name = "policy_checksum", length = 64)
    private String policyChecksum; // Optional hash of policy text

    /* =======================
       Audit Information
       ======================= */
    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "consented_at", nullable = false)
    private LocalDateTime consentedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revoked_by")
    private String revokedBy;

    /* =======================
       Timestamps
       ======================= */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /* =======================
       JPA Lifecycle Hooks
       ======================= */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.consentedAt == null) {
            this.consentedAt = now;
        }
        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* =======================
       Enums
       ======================= */
    public enum ConsentType {
        SIGNATURE,
        FILE_UPLOAD,
        BOTH
    }
}
