package com.org.bgv.vendor.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "verification_method_execution")
public class VerificationMethodExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private VerificationAction action;

    @ManyToOne
    private VerificationMethod method; // EMAIL / EMPLOYER_HR_CONFIRMATION

    @Column
    private String status;
    // INITIATED / RESPONSE_RECEIVED / COMPLETED

    @Column
    private String externalReference;
    // email thread id, portal id, etc.

    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
}
