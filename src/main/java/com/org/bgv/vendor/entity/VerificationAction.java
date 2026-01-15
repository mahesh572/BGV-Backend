package com.org.bgv.vendor.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;

import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.vendor.dto.ActionLevel;
import com.org.bgv.vendor.dto.ActionStatus;
import com.org.bgv.vendor.dto.ActionType;
import com.org.bgv.vendor.dto.ReasonLevel;
import com.org.bgv.vendor.dto.RejectionStatus;

@Entity
@Table(name = "verification_action")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------------------------
    // Action Scope
    // -------------------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActionLevel actionLevel; 
    // CASE, CHECK, OBJECT, DOCUMENT

    // -------------------------
    // Core References
    // -------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private VerificationCase verificationCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_check_id")
    private VerificationCaseCheck verificationCaseCheck;

    // -------------------------
    // Target (only ONE allowed)
    // -------------------------
    @Column(name = "object_id")
    private Long objectId;

    /*
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ObjectType objectType;
    */

    @Column(name = "document_id")
    private Long documentId;

    // -------------------------
    // Action + Reason
    // -------------------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActionType actionType;
    // VERIFY, INSUFFICIENT, REJECT, CLARIFICATION

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id")
    private ActionReason reason;
    
    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;


    @Column(length = 500)
    private String remarks;

    // -------------------------
    // Lifecycle
    // -------------------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActionStatus status;
    // OPEN, RESOLVED

    // -------------------------
    // Audit
    // -------------------------
    @Column(nullable = false)
    private Long performedBy;

    @Column(nullable = false)
    private LocalDateTime performedAt;

    private Long resolvedBy;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
}

