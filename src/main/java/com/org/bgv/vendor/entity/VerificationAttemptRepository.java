package com.org.bgv.vendor.entity;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.vendor.dto.AttemptStatus;
import com.org.bgv.vendor.entity.VerificationAttempt;
import com.org.bgv.vendor.entity.VerificationMethodExecution;

public interface VerificationAttemptRepository
        extends JpaRepository<VerificationAttempt, Long> {

    long countByExecution(VerificationMethodExecution execution);
    
    Optional<VerificationAttempt> findByExecutionAndStatus(
            VerificationMethodExecution execution,
            AttemptStatus status);
    
    boolean existsByExecutionAndStatus(
            VerificationMethodExecution execution,
            AttemptStatus status);
}