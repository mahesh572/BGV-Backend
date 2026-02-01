package com.org.bgv.candidate.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "candidate_identity"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    // 🔐 PAN
    @Column(name = "pan_hash", nullable = false, length = 64)
    private String panHash;

    @Column(name = "pan_last4")
    private String panLast4;

    // 🔐 Aadhaar (optional)
    @Column(name = "aadhaar_hash", length = 64)
    private String aadhaarHash;

    @Column(name = "aadhaar_last4")
    private String aadhaarLast4;

    @Column(name = "verified")
    private Boolean verified = false;

    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
}

