package com.org.bgv.vendor.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;

import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.vendor.dto.RejectionLevel;
import com.org.bgv.vendor.dto.RejectionStatus;

@Entity
@Table(name = "verification_rejection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationRejection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------------------------
    // Scope
    // -------------------------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RejectionLevel level; // CASE, SECTION, OBJECT, DOCUMENT

    // -------------------------
    // Core References
    // -------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private VerificationCase verificationCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_check_id")
    private VerificationCaseCheck verificationCaseCheck; 
    // null only for CASE-level rejection

    // -------------------------
    // Target References (ONLY ONE should be set)
    // -------------------------

    @Column(name = "object_id")
    private Long objectId; 
    // EducationHistory.id OR WorkExperience.id

    @Column(name = "document_id")
    private Long documentId; 
    // VerificationDocument.id

    // -------------------------
    // Reason
    // -------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id", nullable = false)
    private RejectionReason reason;

    @Column(length = 500)
    private String remarks; // Vendor free-text note

    // -------------------------
    // Lifecycle
    // -------------------------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RejectionStatus status;
    // REJECTED, REOPENED, RESOLVED

    // -------------------------
    // Audit
    // -------------------------

    @Column(nullable = false)
    private Long rejectedBy; // vendorUserId / system user

    @Column(nullable = false)
    private LocalDateTime rejectedAt;

    private LocalDateTime resolvedAt;

    private Long resolvedBy;
}
