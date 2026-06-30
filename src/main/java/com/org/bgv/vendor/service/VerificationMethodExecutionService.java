package com.org.bgv.vendor.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.org.bgv.common.RoleConstants;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.enums.ActivitySeverity;
import com.org.bgv.enums.ActivityType;
import com.org.bgv.enums.VendorNoteType;
import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.repository.RoleRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.UserRoleRepository;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.role.dto.RoleDto;
import com.org.bgv.service.ActivityFactory;
import com.org.bgv.service.ActivityTimelineService;
import com.org.bgv.service.RoleService;
import com.org.bgv.vendor.dto.AssignFieldAgentRequest;
import com.org.bgv.vendor.dto.AttemptStatus;
import com.org.bgv.vendor.dto.CreateVerificationExecutionNoteRequest;
import com.org.bgv.vendor.dto.FieldAgentDto;
import com.org.bgv.vendor.dto.StartVerificationMethodRequest;
import com.org.bgv.vendor.dto.UpdateExecutionStatusRequest;
import com.org.bgv.vendor.dto.UpdateVisitLocationRequest;
import com.org.bgv.vendor.entity.FieldVisitAssignment;
import com.org.bgv.vendor.entity.FieldVisitLocation;
import com.org.bgv.vendor.entity.VerificationAttempt;
import com.org.bgv.vendor.entity.VerificationAttemptRepository;
import com.org.bgv.vendor.entity.VerificationExecutionNote;
import com.org.bgv.vendor.entity.VerificationMethod;
import com.org.bgv.vendor.entity.VerificationMethodExecution;
import com.org.bgv.vendor.entity.VerificationMethodExecutionField;
import com.org.bgv.vendor.repository.FieldVisitAssignmentRepository;
import com.org.bgv.vendor.repository.FieldVisitLocationRepository;
import com.org.bgv.vendor.repository.VerificationExecutionNoteRepository;
import com.org.bgv.vendor.repository.VerificationMethodExecutionFieldRepository;
import com.org.bgv.vendor.repository.VerificationMethodExecutionRepository;
import com.org.bgv.vendor.repository.VerificationMethodRepository;
import com.org.bgv.vendor.verification.methods.service.EmailVerificationService;
import com.org.bgv.vendor.verification.methods.service.VerificationContext;
import com.org.bgv.vendor.verification.methods.service.VerificationWorkflowDispatcher;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VerificationMethodExecutionService {

    private final VerificationCaseCheckRepository checkRepository;

    private final VerificationMethodRepository methodRepository;

    private final VerificationMethodExecutionRepository executionRepository;

    private final VerificationMethodExecutionFieldRepository fieldRepository;
    private final VerificationExecutionNoteRepository verificationExecutionNoteRepository;
    private final VerificationExecutionNoteService verificationExecutionNoteService;
    private final VerificationWorkflowDispatcher dispatcher;
    private final VerificationContextUtil verificationContextUtil;
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    
    private final ActivityTimelineService activityTimelineService;
    private final UserRoleRepository userRoleRepository;
    private final FieldVisitAssignmentRepository fieldVisitAssignmentRepository;
    private final UserRepository userRepository;
    private final FieldVisitLocationRepository fieldVisitLocationRepository;
    private final VerificationAttemptRepository verificationAttemptRepository;
    private final VerificationAttemptService verificationAttemptService;
    private final EmailVerificationService emailVerificationService;
   

    
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
        
        
        
        List<VerificationExecutionStatus> terminalStatuses = List.of(
                VerificationExecutionStatus.COMPLETED,
                VerificationExecutionStatus.CANCELLED,
                VerificationExecutionStatus.VISIT_COMPLETED,
                VerificationExecutionStatus.VERIFIED
        );
        boolean alreadyRunning =
                executionRepository
                .existsByVerificationCheckCaseCheckIdAndObjectIdAndVerificationMethodMethodIdAndStatusNotIn(
                        request.getCheckId(),
                        request.getObjectId(),
                        request.getMethodId(),
                        terminalStatuses
                );

        if (alreadyRunning) {
            throw new BusinessException(
                    "Verification method is already running for this object");
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
                        context.getCandidate(),
                        execution.getObjectId()
                )
        );
       

        return savedExecution.getExecutionId();
    }
    
    @Transactional
    public void updateStatus(Long executionId,
                             UpdateExecutionStatusRequest request) {

        log.info("Updating execution status. ExecutionId={}, RequestedStatus={}",
                executionId, request.getStatus());

        VerificationMethodExecution execution =
                executionRepository.findById(executionId)
                        .orElseThrow(() -> {
                            log.error("Execution not found. ExecutionId={}", executionId);
                            return new RuntimeException("Execution not found : " + executionId);
                        });

        VerificationExecutionStatus oldStatus = execution.getStatus();

        log.info("Current execution status. ExecutionId={}, OldStatus={}, NewStatus={}",
                executionId, oldStatus, request.getStatus());

        validateStatusTransition(oldStatus, request.getStatus());
        
        execution.setStatus(request.getStatus());
        
        VerificationContext context =
                verificationContextUtil.build(
                        execution.getVerificationCheck().getCaseCheckId(),
                        execution.getObjectId(),
                        CheckCategoryEnum.fromName(
                                execution.getVerificationCheck()
                                        .getCategory()
                                        .getName())
                                .name());

        if (request.getStatus() == VerificationExecutionStatus.PHONE_CALL_IN_PROGRESS) {

            log.info("Starting new verification attempt. ExecutionId={}", executionId);

            verificationAttemptService.startNewAttempt(executionId);

            log.info("Verification attempt started successfully. ExecutionId={}", executionId);
        }

        else if (request.getStatus() == VerificationExecutionStatus.COMPLETED) {

            log.info("Completing verification attempt. ExecutionId={}, Outcome={}",
                    executionId, request.getOutcome());

            verificationAttemptService.completeAttempt(
                    executionId,
                    request.getOutcome(),
                    request.getNotes());

            log.info("Verification attempt completed. ExecutionId={}", executionId);

            if ("VERIFIED".equalsIgnoreCase(request.getOutcome())) {

                log.info("Verification successful. ExecutionId={}", executionId);

                execution.setStatus(VerificationExecutionStatus.COMPLETED);
                execution.setOutcomeCode(request.getOutcome());
                execution.setOutcomeRemarks(request.getNotes());
                execution.setCompletedAt(LocalDateTime.now());

            } else if (!verificationAttemptService.hasAttemptsRemaining(execution)) {

                log.info("Maximum attempts exhausted. ExecutionId={}", executionId);

                execution.setStatus(VerificationExecutionStatus.COMPLETED);
               // execution.setOutcomeCode("UNABLE_TO_VERIFY");
                execution.setOutcomeRemarks(request.getNotes());
                execution.setCompletedAt(LocalDateTime.now());

            } else {

                log.info("Attempts remaining. Moving execution back to IN_PROGRESS. ExecutionId={}",
                        executionId);

              //  execution.setStatus(VerificationExecutionStatus.IN_PROGRESS);
            }
        }else if(request.getStatus() == VerificationExecutionStatus.SEND_EMAIL || request.getStatus() == VerificationExecutionStatus.RESEND_EMAIL) {
        	
        	emailVerificationService.sendEmail(execution, context);
        	verificationAttemptService.startNewAttempt(executionId);
        	
        }

        executionRepository.save(execution);

        log.info("Execution saved. ExecutionId={}, FinalStatus={}",
                executionId, execution.getStatus());

        

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

                        String.format(
                                "Status changed from %s to %s by vendor",
                                oldStatus,
                                request.getStatus()),

                        SecurityUtils.getCurrentUserId(),

                        "VENDOR",

                        oldStatus.name(),

                        request.getStatus().name(),

                        Map.of(
                                "executionId", executionId,
                                "oldStatus", oldStatus.name(),
                                "newStatus", request.getStatus().name()),

                        context.getCandidate(),
                        execution.getObjectId()));

        log.info("Activity timeline created. ExecutionId={}", executionId);

        if (request.getNotes() != null &&
                !request.getNotes().isBlank()) {

            log.info("Adding execution note. ExecutionId={}", executionId);

            addExecutionNote(execution, request.getNotes());

            log.info("Execution note added. ExecutionId={}", executionId);
        }

        log.info("Execution status update completed successfully. ExecutionId={}", executionId);
    }
    private void validateStatusTransition(
            VerificationExecutionStatus current,
            VerificationExecutionStatus target
    ) {

        if (current == VerificationExecutionStatus.VERIFIED && target!=VerificationExecutionStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Cannot change status of Verified execution");
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
    
    
    
    public List<FieldAgentDto> getFieldAgents() {

        Long companyId = SecurityUtils.getCurrentUserCompanyId();

        log.info("Fetching field agents for companyId={}", companyId);

        List<User> userList = roleService.getusersByCompanyIdAndRoleName(
                companyId,
                RoleConstants.ROLE_FIELD_AGENT);

        log.info("Found {} field agent(s) for companyId={}",
                userList.size(), companyId);

        List<FieldAgentDto> fieldAgents = userList.stream()
                .map(user -> {

                    Profile profile = user.getProfile();

                    String firstName = profile != null ? profile.getFirstName() : "";
                    String lastName = profile != null ? profile.getLastName() : "";

                    log.info(
                            "Mapping Field Agent -> userId={}, email={}, firstName={}, lastName={}",
                            user.getUserId(),
                            user.getEmail(),
                            firstName,
                            lastName
                    );

                    return FieldAgentDto.builder()
                            .userId(user.getUserId())
                            .name((firstName + " " + lastName).trim())
                            .email(user.getEmail())
                            .build();
                })
                .toList();

        log.info("Returning {} field agent DTO(s)", fieldAgents.size());

        return fieldAgents;
    }
    
    @Transactional
    public void assignToFieldAgent(
            Long executionId,
            AssignFieldAgentRequest request) {

        VerificationMethodExecution execution =
                executionRepository.findById(executionId)
                        .orElseThrow(() ->
                                new BusinessException("Execution not found"));

        User agent =
                userRepository.findById(request.getAgentUserId())
                        .orElseThrow(() ->
                                new BusinessException("Field agent not found"));

        String previousAgentName = "";

        FieldVisitAssignment assignment =
                fieldVisitAssignmentRepository
                        .findByExecutionExecutionId(executionId)
                        .orElseGet(FieldVisitAssignment::new);

        // Existing assignment?
        if (assignment.getAssignmentId() != null &&
                assignment.getFieldAgent() != null) {

            previousAgentName =
                    assignment.getFieldAgent().getEmail();
        }

        assignment.setExecution(execution);
        assignment.setFieldAgent(agent);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setScheduledDate(request.getScheduledDate());
        assignment.setRemarks(request.getRemarks());

        fieldVisitAssignmentRepository.save(assignment);

        String oldStatus = execution.getStatus().name();
        
        VerificationExecutionStatus newStatus;

        if (request.getScheduledDate() != null) {
            newStatus = VerificationExecutionStatus.VISIT_SCHEDULED;
        } else {
            newStatus = VerificationExecutionStatus.VISIT_ASSIGNED;
        }

        if (execution.getStatus() != newStatus) {

            execution.setStatus(newStatus);

            executionRepository.save(execution);
        }

        VerificationContext context =
                verificationContextUtil.build(
                        execution.getVerificationCheck().getCaseCheckId(),
                        execution.getObjectId(),
                        CheckCategoryEnum
                                .fromName(
                                        execution.getVerificationCheck()
                                                .getCategory()
                                                .getName())
                                .name());

        boolean reassigned = !previousAgentName.isBlank();

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

                        reassigned
                                ? ActivityType.FIELD_AGENT_REASSIGNED
                                : ActivityType.FIELD_AGENT_ASSIGNED,

                        reassigned
                                ? "Field agent reassigned"
                                : "Field agent assigned",

                        reassigned
                                ? "Field visit reassigned from "
                                  + previousAgentName
                                  + " to "
                                  + agent.getEmail()
                                : "Field visit assigned to "
                                  + agent.getEmail(),

                        SecurityUtils.getCurrentUserId(),

                        "VENDOR",

                        oldStatus,

                        VerificationExecutionStatus.VISIT_ASSIGNED.name(),

                        Map.of(
                                "fieldAgentId", agent.getUserId(),
                                "fieldAgentEmail", agent.getEmail(),
                                "scheduledDate", request.getScheduledDate()
                        ),

                        context.getCandidate(),
                        execution.getObjectId()
                )
        );
    }
    
    @Transactional
    public void updateLocation(
            Long executionId,
            UpdateVisitLocationRequest request) {

        FieldVisitAssignment assignment =
                fieldVisitAssignmentRepository
                        .findByExecutionExecutionId(executionId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Field visit assignment not found"));

        FieldVisitLocation location =
                FieldVisitLocation.builder()
                        .assignment(assignment)
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .address(request.getAddress())
                        .accuracy(request.getAccuracy())
                        .source(request.getSource())
                        .capturedAt(LocalDateTime.now())
                        .capturedBy(SecurityUtils.getCurrentUserId())
                        .build();

        fieldVisitLocationRepository.save(location);
    }
    
    
    
    public static ActivitySeverity from(ActivityType type) {

        switch (type) {

            case MARK_VERIFIED:
            case CASE_COMPLETED:
            case REPORT_SHARED:
                return ActivitySeverity.SUCCESS;

            case MARK_DISCREPANCY_FOUND:
            case ADDRESS_NOT_FOUND:
            case CANDIDATE_NOT_AVAILABLE:
                return ActivitySeverity.WARNING;

            case MARK_UNABLE_TO_VERIFY:
            case DOCUMENT_UPLOAD_FAILED:
                return ActivitySeverity.ERROR;

            default:
                return ActivitySeverity.INFO;
        }
    }
}