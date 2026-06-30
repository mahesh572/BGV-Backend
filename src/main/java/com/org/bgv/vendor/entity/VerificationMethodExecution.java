package com.org.bgv.vendor.entity;

import java.time.LocalDateTime;

import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.enums.VerificationExecutionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "verification_method_execution")
public class VerificationMethodExecution {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long executionId;

    @ManyToOne
    private VerificationAction action;
    
    @ManyToOne
    @JoinColumn(name = "check_id")
    private VerificationCaseCheck verificationCheck;
    
    private Long objectId;

    private String objectType;

    @ManyToOne
    private VerificationMethod verificationMethod; // EMAIL / EMPLOYER_HR_CONFIRMATION

    @Enumerated(EnumType.STRING)
    private VerificationExecutionStatus status;
    // INITIATED / RESPONSE_RECEIVED / COMPLETED
    
    @Column(name = "outcome_code")
    private String outcomeCode;

    @Column(length = 2000)
    private String outcomeRemarks;

    @Column
    private String externalReference;
    // email thread id, portal id, etc.
    
 // Retry management
    private Integer attemptCount;


    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
}
