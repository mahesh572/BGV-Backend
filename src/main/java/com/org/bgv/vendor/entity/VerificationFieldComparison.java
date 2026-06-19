package com.org.bgv.vendor.entity;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.org.bgv.enums.ComparisonStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "verification_field_comparison")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationFieldComparison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Parent object
     * Example:
     * TCS employment
     * B.Tech education
     * PAN card
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id", nullable = false)
    private VerificationObject verificationObject;

    /**
     * instituteName
     * companyName
     * employeeId
     * startDate
     */
    @Column(nullable = false)
    private String fieldName;

    /**
     * Institute Name
     */
    private String displayName;

    /**
     * Value submitted by candidate
     */
    @Column(length = 2000)
    private String candidateValue;

    /**
     * Value received from source
     */
    @Column(length = 2000)
    private String sourceValue;

    /**
     * MATCH / MISMATCH / NOT_VERIFIED / NOT_AVAILABLE
     */
    @Enumerated(EnumType.STRING)
    private ComparisonStatus result;

    /**
     * Remarks by verifier
     */
    @Column(length = 1000)
    private String remarks;

    /**
     * Whether vendor has verified this field
     */
    private Boolean verified = false;

    private Long verifiedBy;

    private LocalDateTime verifiedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
