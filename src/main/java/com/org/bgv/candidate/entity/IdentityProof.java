package com.org.bgv.candidate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.org.bgv.entity.Profile;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;

@Entity
@Table(name = "identity_proofs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentityProof {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_check_id", nullable = false)
    private VerificationCaseCheck verificationCaseCheck;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private VerificationCase verificationCase;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
    

    @Column(name = "document_number", length = 100)
    private String documentNumber;
    
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private Long docTypeId;
    
    @Column(name = "verified")
    private Boolean verified = false;
    
    @Column(name = "verification_status", length = 50)
    private String verificationStatus = "pending";
    
    @Column(name = "verified_by", length = 100)
    private String verifiedBy;
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(name = "status", length = 50)
    private String status; // e.g. PENDING, VERIFIED, REJECTED

    @Column(name = "updated_by", length = 100)
    private String updatedBy;
    
    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }
    public boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        return expiryDate.isBefore(LocalDate.now());
    }
    
    public long getDaysUntilExpiry() {
        if (expiryDate == null) {
            return Long.MAX_VALUE;
        }
        
        LocalDate today = LocalDate.now();
        if (today.isAfter(expiryDate)) {
            return 0;
        }
        
        return java.time.temporal.ChronoUnit.DAYS.between(today, expiryDate);
    }
    
    public int getAgeInYears() {
        if (issueDate == null) {
            return 0;
        }
        
        LocalDate today = LocalDate.now();
        return (int) java.time.temporal.ChronoUnit.YEARS.between(issueDate, today);
    }
}
