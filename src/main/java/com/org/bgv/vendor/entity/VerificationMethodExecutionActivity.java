package com.org.bgv.vendor.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
public class VerificationMethodExecutionActivity {

    @Id
    private Long id;

    @ManyToOne
    private VerificationMethodExecution execution;

    private String action;

    private String remarks;

    private LocalDateTime createdAt;

}