package com.org.bgv.vendor.entity;

import java.time.LocalDateTime;

import com.org.bgv.entity.User;
import com.org.bgv.vendor.dto.AttemptStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationAttempt {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptId;

    @ManyToOne
    @JoinColumn(name="execution_id")
    private VerificationMethodExecution execution;

    private Integer attemptNumber;

    @Enumerated(EnumType.STRING)
    private AttemptStatus status;

    private String outcomeCode;

    private String remarks;

    @ManyToOne
    private User performedBy;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;
}