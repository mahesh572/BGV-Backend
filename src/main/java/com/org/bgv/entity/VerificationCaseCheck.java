package com.org.bgv.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.vendor.entity.VendorUser;
import com.org.bgv.vendor.entity.VerificationAction;

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
    
    @Column(name = "check_ref", unique = true, nullable = false)
    private String checkRef; // CASE-25

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
    
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "started_at")
    @Builder.Default
    private LocalDateTime startedAt = LocalDateTime.now();
    
    @Column(name = "completed_at")
    @Builder.Default
    private LocalDateTime completedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_company_id")
    private Company vendorCompany;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_user_id")
    private User assignedVendorUser;
    
    
    @OneToMany(mappedBy = "verificationCaseCheck", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VerificationCaseDocument> documents = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_action_id")
    private VerificationAction lastAction;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private LocalDateTime assignedAt;
   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private User assignedBy;
}
