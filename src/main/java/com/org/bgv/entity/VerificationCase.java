package com.org.bgv.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.org.bgv.constants.CaseStatus;

@Entity
@Table(name = "verification_case")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationCase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_id")
    private Long caseId;
    
    @Column(name = "case_number", unique = true, nullable = false)
    private String caseNumber; // CASE-25

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_package_id", nullable = false)
    private EmployerPackage employerPackage;

    @Column(name = "base_price", nullable = false)
    private Double basePrice;
    
    @Column(name = "addon_price")
    @Builder.Default
    private Double addonPrice = 0.0;
    
    @Column(name = "total_price", nullable = false)
    private Double totalPrice;
    
    @Enumerated(EnumType.STRING)
    private CaseStatus status = CaseStatus.INITIATED;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @OneToMany(mappedBy = "verificationCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VerificationCaseDocument> caseDocuments = new ArrayList<>();
    
    @OneToMany(mappedBy = "verificationCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VerificationCaseCheck> caseChecks = new ArrayList<>();
    
    /*
    @Column(name = "vendor_id")
    private Long vendorId;
   */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
