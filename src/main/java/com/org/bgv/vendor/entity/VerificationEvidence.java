package com.org.bgv.vendor.entity;

import java.time.LocalDateTime;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.common.EvidenceLevel;
import com.org.bgv.common.EvidenceStatus;
import com.org.bgv.constants.VerificationStatus;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "verification_evidence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ===== Context ===== */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verification_case_id", nullable = false)
    private VerificationCase verificationCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verification_case_check_id", nullable = false)
    private VerificationCaseCheck verificationCaseCheck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CheckCategory category;

    /* ===== Optional Binding ===== */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_type_id")
    private DocumentType documentType; // nullable

    @Column(name = "object_id")
    private Long objectId; // nullable (docType object like IdentityProof id)
    
    @Column(name = "docId")
    private Long docId;

    /* ===== Evidence File ===== */

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "aws_doc_key")
    private String awsDocKey;

    /* ===== Metadata ===== */

    @Enumerated(EnumType.STRING)
    @Column(name = "evidence_level", nullable = false)
    private EvidenceLevel evidenceLevel; 
    // SECTION / DOC_TYPE

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "uploaded_by_id")
    private Long uploadedById;
    
    @Column(name = "uploaded_by_role")
    private String uploadedByRole; // VENDOR / SYSTEM / ADMIN

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EvidenceStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rejection_id")
    private VerificationRejection rejection;
    
    @Column(name = "archived", nullable = false)
    private boolean archived = false;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
        this.status = EvidenceStatus.UPLOADED;
    }
}

