package com.org.bgv.policy.entity;

import java.time.LocalDateTime;

import com.org.bgv.policy.enums.ConsentStatus;
import com.org.bgv.policy.enums.ConsentType;
import com.org.bgv.policy.enums.EntityType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "policy_consent",
    indexes = {
        @Index(name = "idx_policy_consent_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_policy_consent_policy_version", columnList = "policy_version_id"),
        @Index(name = "idx_policy_consent_reference", columnList = "reference_number"),
        @Index(name = "idx_policy_consent_status", columnList = "status")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_policy_consent_entity_policy",
            columnNames = {
                "entity_type",
                "entity_id",
                "policy_version_id"
            }
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyConsent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "policy_consent_id", updatable = false, nullable = false)
    private String id;

    /**
     * Student / Candidate / Employer / Vendor
     */
    @Column(name = "entity_id", nullable = false, length = 100)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 30)
    private EntityType entityType;

    /**
     * Accepted policy version.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_version_id", nullable = false)
    private PolicyVersion policyVersion;

    /**
     * Future-proof.
     * POLICY
     * BACKGROUND_VERIFICATION
     * BIOMETRIC
     * DOCUMENT_SHARING
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "consent_type", nullable = false)
    private ConsentType consentType;

    /**
     * User clicked "I Agree"
     */
    @Column(name = "consent_given", nullable = false)
    private Boolean consentGiven;

    /**
     * Signature
     */
    @Column(name = "signature_url")
    private String signatureUrl;

    @Column(name = "signature_s3_key")
    private String signatureS3Key;

    @Column(name = "signature_hash", length = 64)
    private String signatureHash;

    /**
     * Live Photo
     */
    @Column(name = "live_photo_url")
    private String livePhotoUrl;

    @Column(name = "live_photo_s3_key")
    private String livePhotoS3Key;

    @Column(name = "live_photo_hash", length = 64)
    private String livePhotoHash;

    /**
     * Generated Consent PDF
     */
    @Column(name = "consent_pdf_url")
    private String consentPdfUrl;

    @Column(name = "consent_pdf_s3_key")
    private String consentPdfS3Key;

    @Column(name = "pdf_hash", length = 64)
    private String pdfHash;

    /**
     * PDF Reference Number
     */
    @Column(name = "reference_number", unique = true, nullable = false)
    private String referenceNumber;

    /**
     * SHA-256 hash of policy content.
     */
    @Column(name = "policy_checksum", length = 64)
    private String policyChecksum;

    /**
     * Audit
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConsentStatus status;

    @Column(name = "accepted_at", nullable = false)
    private LocalDateTime acceptedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revoked_by")
    private String revokedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (acceptedAt == null) {
            acceptedAt = now;
        }

        if (status == null) {
            status = ConsentStatus.ACTIVE;
        }

        if (consentGiven == null) {
            consentGiven = Boolean.TRUE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}