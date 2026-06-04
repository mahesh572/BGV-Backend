package com.org.bgv.vendor.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.vendor.dto.StartVerificationMethodRequest;
import com.org.bgv.vendor.dto.UpdateExecutionStatusRequest;
import com.org.bgv.vendor.entity.VerificationMethod;
import com.org.bgv.vendor.entity.VerificationMethodExecution;
import com.org.bgv.vendor.entity.VerificationMethodExecutionField;
import com.org.bgv.vendor.repository.VerificationMethodExecutionFieldRepository;
import com.org.bgv.vendor.repository.VerificationMethodExecutionRepository;
import com.org.bgv.vendor.repository.VerificationMethodRepository;
import com.org.bgv.vendor.verification.methods.service.VerificationContext;
import com.org.bgv.vendor.verification.methods.service.VerificationWorkflowDispatcher;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VerificationMethodExecutionService {

    private final VerificationCaseCheckRepository checkRepository;

    private final VerificationMethodRepository methodRepository;

    private final VerificationMethodExecutionRepository executionRepository;

    private final VerificationMethodExecutionFieldRepository fieldRepository;
    private final VerificationWorkflowDispatcher dispatcher;
    private final VerificationContextUtil verificationContextUtil;

    public Long startVerification(
            StartVerificationMethodRequest request) {

        VerificationCaseCheck check =
                checkRepository.findById(request.getCheckId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Verification check not found: "
                                                + request.getCheckId()));

        VerificationMethod method =
                methodRepository.findById(request.getMethodId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Verification method not found: "
                                                + request.getMethodId()));

        VerificationMethodExecution execution =
                new VerificationMethodExecution();

        execution.setVerificationCheck(check);
        execution.setObjectId(request.getObjectId());
        execution.setObjectType(request.getCheckType());
        execution.setVerificationMethod(method);
        execution.setStatus(
                VerificationExecutionStatus.IN_PROGRESS);

        execution.setInitiatedAt(
                request.getStartedAt());

        VerificationMethodExecution savedExecution =
                executionRepository.save(execution);
        
       

        if (request.getFields() != null
                && !request.getFields().isEmpty()) {

            for (Map.Entry<String, Object> entry :
                    request.getFields().entrySet()) {

                VerificationMethodExecutionField field =
                        new VerificationMethodExecutionField();

                field.setExecution(savedExecution);

                field.setFieldName(
                        entry.getKey());

                field.setFieldValue(
                        entry.getValue() == null
                                ? null
                                : String.valueOf(
                                        entry.getValue()));

                fieldRepository.save(field);
            }
        }
        
       VerificationContext context = verificationContextUtil.build(check.getCaseCheckId(), request.getObjectId(), request.getCheckType());
        
        dispatcher.dispatch(savedExecution,context);
        

        return savedExecution.getExecutionId();
    }
    
    @Transactional
    public void updateStatus(Long executionId, UpdateExecutionStatusRequest request) {

        VerificationMethodExecution execution =
                executionRepository.findById(executionId)
                        .orElseThrow(() -> new RuntimeException(
                                "Execution not found: " + executionId
                        ));

        // Optional: validation (recommended)
        validateStatusTransition(execution.getStatus(), request.getStatus());

        // Update status
        execution.setStatus(request.getStatus());

        // If moving to RESPONSE_RECEIVED or COMPLETED, you may set timestamp logic
        if (request.getStatus() == VerificationExecutionStatus.RESPONSE_RECEIVED) {
           // execution.setResponseReceivedAt(LocalDateTime.now());
        }

        if (request.getStatus() == VerificationExecutionStatus.COMPLETED) {
            execution.setCompletedAt(LocalDateTime.now());
        }

        // Save execution
        executionRepository.save(execution);

        // Store notes (recommended separate table or audit log)
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            addExecutionNote(execution, request.getNotes());
        }
    }
    
    private void validateStatusTransition(
            VerificationExecutionStatus current,
            VerificationExecutionStatus target
    ) {

        if (current == VerificationExecutionStatus.COMPLETED) {
            throw new IllegalStateException("Cannot change status of completed execution");
        }

        if (current == VerificationExecutionStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of cancelled execution");
        }

        // Add more rules if needed
    }

    private void addExecutionNote(VerificationMethodExecution execution, String note) {
        // Example: store in audit table or notes table
        System.out.println("NOTE for execution " + execution.getExecutionId() + ": " + note);
    }
}