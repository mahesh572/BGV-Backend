package com.org.bgv.vendor.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.org.bgv.config.SecurityUtils;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.enums.ActivityType;
import com.org.bgv.enums.VendorNoteType;
import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.service.ActivityFactory;
import com.org.bgv.service.ActivityTimelineService;
import com.org.bgv.vendor.dto.CreateVerificationExecutionNoteRequest;
import com.org.bgv.vendor.dto.StartVerificationMethodRequest;
import com.org.bgv.vendor.dto.UpdateExecutionStatusRequest;
import com.org.bgv.vendor.entity.VerificationExecutionNote;
import com.org.bgv.vendor.entity.VerificationMethod;
import com.org.bgv.vendor.entity.VerificationMethodExecution;
import com.org.bgv.vendor.entity.VerificationMethodExecutionField;
import com.org.bgv.vendor.repository.VerificationExecutionNoteRepository;
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
    private final VerificationExecutionNoteRepository verificationExecutionNoteRepository;
    private final VerificationExecutionNoteService verificationExecutionNoteService;
    private final VerificationWorkflowDispatcher dispatcher;
    private final VerificationContextUtil verificationContextUtil;
    
    private final ActivityTimelineService activityTimelineService;

    
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
        
        
        List<VerificationExecutionStatus> activeStatuses =
                List.of(
                        VerificationExecutionStatus.IN_PROGRESS,
                        VerificationExecutionStatus.WAITING_FOR_RESPONSE,
                        VerificationExecutionStatus.RESPONSE_RECEIVED,
                        VerificationExecutionStatus.UNDER_REVIEW
                );

        boolean alreadyRunning =
                executionRepository
                .existsByVerificationCheckCaseCheckIdAndObjectIdAndVerificationMethodMethodIdAndStatusIn(
                        request.getCheckId(),
                        request.getObjectId(),
                        request.getMethodId(),
                        activeStatuses
                );

        if (alreadyRunning) {
            throw new BusinessException(
                    "Verification method is already running for this object"
            );
        }

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
        
        activityTimelineService.log(
                ActivityFactory.create(
                        check.getVerificationCase().getCaseId(),
                        check.getCaseCheckId(),
                        savedExecution.getExecutionId(),
                        null,
                        null,
                        ActivityType.EXECUTION_STARTED,
                        "Execution started",
                        "Vendor started verification method",
                        SecurityUtils.getCurrentUserId(),
                        "VENDOR",
                        null,
                        VerificationExecutionStatus.IN_PROGRESS.name(),
                        Map.of(
                                "methodId", method.getMethodId(),
                                "methodName", method.getName()
                        ),
                        context.getCandidate()
                )
        );
       

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
        
        
        VerificationContext context = verificationContextUtil.build(execution.getVerificationCheck().getCaseCheckId(), execution.getObjectId(),  CheckCategoryEnum.fromName(execution.getVerificationCheck().getCategory().getName()).name());
        
        activityTimelineService.log(
                ActivityFactory.create(
                        execution.getVerificationCheck()
                                .getVerificationCase()
                                .getCaseId(),

                        execution.getVerificationCheck()
                                .getCaseCheckId(),

                        executionId,
                        null,
                        null,

                        ActivityType.STATUS_CHANGED,

                        "Execution status updated",

                        "Status changed by vendor",

                        SecurityUtils.getCurrentUserId(),

                        "VENDOR",

                        execution.getStatus().name(),

                        request.getStatus().name(),

                        Map.of(
                                "executionId", executionId
                        ),

                        context.getCandidate()
                )
        );

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
    	
    	/*
    	VerificationExecutionNote executionNote =
    	        VerificationExecutionNote.builder()
    	                .execution(execution)
    	                .caseId(
    	                    execution.getVerificationCheck()
    	                             .getVerificationCase()
    	                             .getCaseId())
    	                .checkId(
    	                    execution.getVerificationCheck()
    	                             .getCaseCheckId())
    	                .execution(
    	                    execution)
    	                .objectId(
    	                    execution.getObjectId())
    	                .objectType(
    	                    execution.getObjectType())
    	                .note(note)
    	                .createdBy(SecurityUtils.getCurrentUserId())
    	                .createdByRole("VENDOR")
    	                .build();
    	
    	*/
    	CreateVerificationExecutionNoteRequest verificationExecutionNoteRequest = CreateVerificationExecutionNoteRequest.builder()
    			.executionId(execution.getExecutionId())
    			.note(note)
    			.type(VendorNoteType.GENERAL)
    			.build();
    	
    	
    	
    	verificationExecutionNoteService.addNote(verificationExecutionNoteRequest);
    	
    	// verificationExecutionNoteRepository.save(executionNote);
    }
}