package com.org.bgv.vendor.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.vendor.dto.AttemptStatus;
import com.org.bgv.vendor.entity.VerificationAttempt;
import com.org.bgv.vendor.entity.VerificationAttemptRepository;
import com.org.bgv.vendor.entity.VerificationMethodExecution;
import com.org.bgv.vendor.repository.VerificationMethodExecutionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationAttemptService {

    private final VerificationMethodExecutionRepository executionRepository;
    private final VerificationAttemptRepository attemptRepository;

    @Transactional
    public VerificationAttempt startNewAttempt(Long executionId) {

        VerificationMethodExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new BusinessException("Verification execution not found"));

        // Check if an active attempt already exists
        if (attemptRepository.existsByExecutionAndStatus(execution, AttemptStatus.IN_PROGRESS)) {
        	Optional<VerificationAttempt> activeAttempt =
        	        attemptRepository.findByExecutionAndStatus(execution, AttemptStatus.IN_PROGRESS);

        	activeAttempt.ifPresent(attempt -> {
        	    attempt.setStatus(AttemptStatus.COMPLETED); // or COMPLETED/EXPIRED
        	    attempt.setCompletedAt(LocalDateTime.now());
        	    attemptRepository.save(attempt);
        	});
        }

        int maxAttempts = execution.getVerificationMethod().getMaxAttempts();

        long currentAttempts = attemptRepository.countByExecution(execution);

        if (currentAttempts >= maxAttempts) {
            throw new BusinessException("Maximum attempts reached");
        }

        VerificationAttempt attempt = VerificationAttempt.builder()
                .execution(execution)
                .attemptNumber((int) currentAttempts + 1)
                .status(AttemptStatus.IN_PROGRESS)
                .startedAt(LocalDateTime.now())
                .build();

        return attemptRepository.save(attempt);
    }

    @Transactional
    public void completeAttempt(Long executionId,
                                String outcome,
                                String remarks) {

        VerificationMethodExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new BusinessException("Verification execution not found"));

        VerificationAttempt attempt = attemptRepository
                .findByExecutionAndStatus(execution, AttemptStatus.IN_PROGRESS).orElse(null);
                
        if(attempt!=null) {

        attempt.setStatus(AttemptStatus.COMPLETED);
        attempt.setOutcomeCode(outcome);
        attempt.setRemarks(remarks);
        attempt.setCompletedAt(LocalDateTime.now());

        attemptRepository.save(attempt);
        }
    }

    public boolean hasAttemptsRemaining(VerificationMethodExecution execution) {

        long attempts = attemptRepository.countByExecution(execution);

        return attempts < execution.getVerificationMethod().getMaxAttempts();
    }
}