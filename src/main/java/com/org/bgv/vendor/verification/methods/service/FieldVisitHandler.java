package com.org.bgv.vendor.verification.methods.service;

import org.springframework.stereotype.Service;

import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.vendor.entity.VerificationMethodExecution;
import com.org.bgv.vendor.repository.VerificationMethodExecutionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FieldVisitHandler
        implements VerificationMethodHandler {

    private final VerificationMethodExecutionRepository executionRepository;

    @Override
    public void execute(
            VerificationMethodExecution execution,VerificationContext context) {

        execution.setStatus(
                VerificationExecutionStatus.INITIATED);

        executionRepository.save(execution);
    }
}

/*

Start
 ↓
Execution Created
 ↓
ASSIGNED
 ↓
Field Agent Visit
 ↓
Photos Uploaded
 ↓
Visit Report
 ↓
VERIFIED

*/