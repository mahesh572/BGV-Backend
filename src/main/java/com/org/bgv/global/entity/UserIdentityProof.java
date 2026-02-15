package com.org.bgv.global.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_identity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserIdentityProof {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "document_type_id", nullable = false)
    private Long docTypeId;

    @Column(name = "document_number", length = 100)
    private String documentNumber;

    private LocalDate issueDate;
    private LocalDate expiryDate;

    @Column(name = "is_primary")
    private boolean primary;
    
    @Column(name = "pan_hash", length = 64)
    private String panHash;

    @Column(name = "aadhaar_hash", length = 64)
    private String aadhaarHash;

    @Column(name = "identity_verified")
    private Boolean identityVerified;

    @Column(name = "identity_verified_at")
    private LocalDateTime identityVerifiedAt;

    @Column(name = "identity_verified_by")
    private String identityVerifiedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /* ---------- Derived helpers ---------- */

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    public long getDaysUntilExpiry() {
        if (expiryDate == null) return Long.MAX_VALUE;
        return java.time.temporal.ChronoUnit.DAYS
                .between(LocalDate.now(), expiryDate);
    }
}
