package com.org.bgv.entity;

import com.org.bgv.constants.CaseCheckStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "verification_case_check")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationCaseCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_check_id")
    private Long caseCheckId;

    /**
     * Parent verification case
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private VerificationCase verificationCase;

    /**
     * Verification check (Education, Employment, etc.)
     */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CheckCategory category;

    /**
     * Current verification status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CaseCheckStatus status;
    
    
}
