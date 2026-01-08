package com.org.bgv.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_consent")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateConsent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "consent_type", nullable = false)
    private ConsentType consentType;

    // For canvas signature
    @Column(name = "signature_data", columnDefinition = "TEXT")
    private String signatureData; // JSON coordinates if needed
    
    @Column(name = "signature_url")
    private String signatureUrl; // S3 URL for signature image
    
    @Column(name = "signature_s3_key")
    private String signatureS3Key; // S3 object key for signature

    // For file upload
    @Column(name = "document_url")
    private String documentUrl; // S3 URL for uploaded document
    
    @Column(name = "document_s3_key")
    private String documentS3Key; // S3 object key for document
    
    @Column(name = "original_file_name")
    private String originalFileName;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Column(name = "file_size")
    private Long fileSize;

    // Audit fields
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "policy_version", nullable = false)
    private String policyVersion;
    
    @Column(name = "consented_at", nullable = false)
    private LocalDateTime consentedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.consentedAt == null) {
            this.consentedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum ConsentType {
        SIGNATURE, 
        FILE_UPLOAD, 
        BOTH
    }
}