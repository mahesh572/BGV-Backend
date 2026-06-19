package com.org.bgv.vendor.entity;


import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.enums.VerificationObjectStatus;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_object")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_check_id")
    private VerificationCaseCheck verificationCheck;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckCategoryEnum objectType;

    // ID from education_history, work_experience etc.
    @Column(nullable = false)
    private Long sourceId;

    // Display name shown in UI
    private String objectName;

    @Enumerated(EnumType.STRING)
    private VerificationObjectStatus status;

    // CLEAR / DISCREPANCY / UNABLE_TO_VERIFY
    @Enumerated(EnumType.STRING)
    private VerificationObjectStatus finalResult;

    private Boolean candidateSubmitted;

    private Long verifiedBy;

    private LocalDateTime verifiedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Column(length = 2000)
    private String remarks;
}
