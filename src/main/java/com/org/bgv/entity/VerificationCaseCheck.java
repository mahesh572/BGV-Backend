package com.org.bgv.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;

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
    
    @Column(name = "vendor_id")
    private Long vendorId;
    
    @OneToMany(mappedBy = "verificationCaseCheck", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VerificationCaseDocument> documents = new ArrayList<>();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private LocalDateTime assignedAt;
    private Long assignedBy;
}
