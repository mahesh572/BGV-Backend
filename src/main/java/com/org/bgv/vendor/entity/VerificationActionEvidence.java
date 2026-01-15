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
import com.org.bgv.vendor.dto.EvidenceSource;

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
@Table(name = "verification_action_evidence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationActionEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =====================
       Parent Action
       ===================== */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id")
    private VerificationAction action;

    /* =====================
       Evidence Source
       ===================== */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EvidenceSource source;
    // CANDIDATE_DOCUMENT / VENDOR_UPLOAD

    /* =====================
       Candidate-linked Evidence
       ===================== */
    @Column(name = "document_id")
    private Long documentId;
    // References VerificationDocument.id

    /* =====================
       Vendor-uploaded Evidence
       ===================== */
    private String fileName;
    private String originalFileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String storageKey;

    
    /* =====================
       Audit
       ===================== */
    @Column(nullable = false)
    private Long uploadedBy;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private boolean archived;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
        this.archived = false;
    }
}

