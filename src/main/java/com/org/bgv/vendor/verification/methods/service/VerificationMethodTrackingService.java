package com.org.bgv.vendor.verification.methods.service;


import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.enums.VerificationExecutionAction;
import com.org.bgv.enums.VerificationMethodCode;
import com.org.bgv.vendor.dto.ExecutionActionDto;
import com.org.bgv.vendor.dto.FieldVisitAssignmentDto;
import com.org.bgv.vendor.dto.VerificationExecutionNoteDto;
import com.org.bgv.vendor.dto.VerificationMethodExecutionDetailsDto;
import com.org.bgv.vendor.dto.VerificationMethodFieldDTO;
import com.org.bgv.vendor.entity.VerificationMethodExecution;
import com.org.bgv.vendor.entity.VerificationMethodExecutionField;
import com.org.bgv.vendor.entity.VerificationMethodField;
import com.org.bgv.vendor.repository.FieldVisitAssignmentRepository;
import com.org.bgv.vendor.repository.VerificationExecutionNoteRepository;
import com.org.bgv.vendor.repository.VerificationMethodExecutionFieldRepository;
import com.org.bgv.vendor.repository.VerificationMethodExecutionRepository;
import com.org.bgv.vendor.repository.VerificationMethodFieldRepository;
import com.org.bgv.vendor.service.ExecutionActionConfigService;
import com.org.bgv.vendor.service.VerificationExecutionNoteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationMethodTrackingService {

    private final VerificationMethodExecutionRepository executionRepository;

    private final VerificationMethodExecutionFieldRepository fieldRepository;
    private final VerificationMethodFieldRepository methodFieldRepository;
    private final ExecutionActionConfigService executionActionConfigService;
    private final VerificationExecutionNoteRepository verificationExecutionNoteRepository;
    private final VerificationExecutionNoteService verificationExecutionNoteService;
    private final FieldVisitAssignmentRepository fieldVisitAssignmentRepository;

    public List<VerificationMethodExecutionDetailsDto>
    getExecutions(Long checkId) {

        return executionRepository
                .findByVerificationCheckCaseCheckId(checkId)
                .stream()
                .map(this::mapExecution)
                .toList();
    }

    public VerificationMethodExecutionDetailsDto getExecution(Long executionId) {

        VerificationMethodExecution execution =
                executionRepository.findById(executionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Execution not found"));

        return mapExecution(execution);
    }

    private VerificationMethodExecutionDetailsDto mapExecution(
            VerificationMethodExecution execution) {

        log.info("Mapping executionId={} method={}",
                execution.getExecutionId(),
                execution.getVerificationMethod().getCode());

        List<VerificationMethodExecutionField> values =
                fieldRepository.findByExecutionExecutionId(
                        execution.getExecutionId());

        log.debug("Found {} fields for executionId={}",
                values.size(),
                execution.getExecutionId());

        List<VerificationMethodFieldDTO> fields =
                values.stream()
                        .map(field ->
                                VerificationMethodFieldDTO.builder()
                                        .fieldName(field.getFieldName())
                                        .fieldLabel(field.getFieldName())
                                        .fieldType("TEXT")
                                        .value(field.getFieldValue())
                                        .build())
                        .toList();

        log.debug("Fetching notes for executionId={}",
                execution.getExecutionId());

        List<VerificationExecutionNoteDto> notes =
                verificationExecutionNoteService
                        .getExecutionNotes(execution.getExecutionId());

        log.debug("Found {} notes for executionId={}",
                notes.size(),
                execution.getExecutionId());

        VerificationMethodExecutionDetailsDto dto =
                VerificationMethodExecutionDetailsDto.builder()
                        .executionId(execution.getExecutionId())
                        .methodCode(
                                execution.getVerificationMethod().getCode())
                        .methodName(
                                execution.getVerificationMethod().getName())
                        .status(execution.getStatus())
                        .initiatedAt(execution.getInitiatedAt())
                        .completedAt(execution.getCompletedAt())
                        .fields(fields)
                        .notes(notes)
                        .build();

        log.info(
                "Execution mapped successfully. executionId={}, fields={}, notes={}",
                execution.getExecutionId(),
                fields.size(),
                notes.size());

        return dto;
    }
    
    public List<VerificationMethodExecutionDetailsDto>
    getExecutions(
            Long checkId,
            Long objectId) {

        return executionRepository
                .findByVerificationCheckCaseCheckIdAndObjectIdOrderByInitiatedAtDesc(
                        checkId,
                        objectId)
                .stream()
                .map(this::toDto)
                .toList();
    }

   

    private VerificationMethodExecutionDetailsDto toDto(
            VerificationMethodExecution execution) {

        List<VerificationMethodExecutionField> executionFields =
                fieldRepository.findByExecutionExecutionId(
                        execution.getExecutionId());

        Map<String, String> values =
                executionFields.stream()
                        .collect(Collectors.toMap(
                                VerificationMethodExecutionField::getFieldName,
                                VerificationMethodExecutionField::getFieldValue,
                                (a, b) -> b
                        ));

        List<VerificationMethodField> methodFields =
                methodFieldRepository
                        .findByVerificationMethodMethodIdOrderByDisplayOrderAsc(
                                execution.getVerificationMethod()
                                        .getMethodId());
        
        List<VerificationExecutionNoteDto> notes =
                verificationExecutionNoteService
                        .getExecutionNotes(execution.getExecutionId());
        
        
        

        List<VerificationMethodFieldDTO> fields =
                methodFields.stream()
                        .map(field ->
                                VerificationMethodFieldDTO.builder()
                                        .fieldId(field.getId())
                                        .fieldName(field.getFieldName())
                                        .fieldLabel(field.getFieldLabel())
                                        .fieldType(field.getFieldType())
                                        .requiredField(field.getRequiredField())
                                        .displayOrder(field.getDisplayOrder())
                                        .placeholder(field.getPlaceholder())
                                        .value(
                                                values.get(
                                                        field.getFieldName()))
                                        .build()
                        )
                        .toList();
        
        FieldVisitAssignmentDto assignmentDto = null;

        if (VerificationMethodCode.FIELD_VISIT.equals(
                execution.getVerificationMethod().getCode())) {

            assignmentDto =
                    fieldVisitAssignmentRepository
                            .findByExecutionExecutionId(
                                    execution.getExecutionId())
                            .map(assignment ->

                                    FieldVisitAssignmentDto.builder()
                                    .assignmentId(assignment.getAssignmentId())
                                    .fieldAgentId(assignment.getFieldAgent().getUserId())
                                    .fieldAgentName("N/A")
                                    .fieldAgentEmail(assignment.getFieldAgent().getEmail())
                                  //  .fieldAgentPhone(assignment.getFieldAgent().getPhoneNumber())
                                    .assignedAt(assignment.getAssignedAt())
                                    .scheduledDate(assignment.getScheduledDate())
                                    .remarks(assignment.getRemarks())
                                            .build()

                            )
                            .orElse(null);
        }

        return VerificationMethodExecutionDetailsDto.builder()
                .executionId(execution.getExecutionId())
                .checkId(
                        execution.getVerificationCheck()
                                .getCaseCheckId())
                .methodCode(
                        execution.getVerificationMethod()
                                .getCode())
                .methodName(
                        execution.getVerificationMethod()
                                .getName())
                .status(execution.getStatus())
                .initiatedAt(execution.getInitiatedAt())
                .completedAt(execution.getCompletedAt())
                .fields(fields)
                .allowedActions(
                	    executionActionConfigService.getActions(execution.getVerificationMethod()
                                .getCode(),execution.getStatus())
                	        .stream()
                	        .map(this::mapAction)
                	        .toList()
                	)
                .fieldVisitAssignment(assignmentDto)
                .notes(notes)
                .build();
    }
    
    private ExecutionActionDto mapAction(VerificationExecutionAction action) {
    	
    	
    	log.info("VerificationExecutionAction:::::::::::::::::::::::::::",action);

        return switch (action) {

            case UPLOAD_EVIDENCE -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Upload Evidence")
                    .type("PRIMARY")
                    .build();

            case SEND_REMINDER -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Send Reminder")
                    .type("SECONDARY")
                    .build();

            case MARK_RESPONSE_RECEIVED -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Mark Response Received")
                    .type("SUCCESS")
                    .build();

            case ADD_NOTE -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Add Note")
                    .type("SECONDARY")
                    .build();

            case CONTACT_CANDIDATE -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Contact Candidate")
                    .type("SECONDARY")
                    .build();

            case CANCEL -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Cancel")
                    .type("DANGER")
                    .build();

            case COMPLETE -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Complete Execution")
                    .type("SUCCESS")
                    .build();

            case MARK_UNDER_REVIEW -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Mark Under Review")
                    .type("WARNING")
                    .build();
            case MAKE_PHONE_CALL -> ExecutionActionDto.builder()
            .action(action.name())
            .label("Make Phone Call")
            .type("WARNING")
            .build();
         // ===== Field Visit =====

            case ASSIGN_FIELD_AGENT -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Assign Field Agent")
                    .type("PRIMARY")
                    .build();

            case CHANGE_FIELD_AGENT -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Change Field Agent")
                    .type("WARNING")
                    .build();

            case SCHEDULE_VISIT -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Schedule Visit")
                    .type("PRIMARY")
                    .build();

            case RESCHEDULE_VISIT -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Reschedule Visit")
                    .type("WARNING")
                    .build();

            case START_VISIT -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Start Visit")
                    .type("PRIMARY")
                    .build();

            case MARK_VISIT_COMPLETED -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Mark Visit Completed")
                    .type("SUCCESS")
                    .build();

            case MARK_ADDRESS_NOT_FOUND -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Address Not Found")
                    .type("WARNING")
                    .build();

            case MARK_CANDIDATE_NOT_AVAILABLE -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Candidate Not Available")
                    .type("WARNING")
                    .build();

            case MARK_VERIFIED -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Mark Verified")
                    .type("SUCCESS")
                    .build();

            case MARK_DISCREPANCY_FOUND -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Mark Discrepancy Found")
                    .type("DANGER")
                    .build();

            case MARK_UNABLE_TO_VERIFY -> ExecutionActionDto.builder()
                    .action(action.name())
                    .label("Mark Unable To Verify")
                    .type("WARNING")
                    .build();
            case CAPTURE_LOCATION -> ExecutionActionDto.builder()
            .action(action.name())
            .label("Capture Location")
            .type("PRIMARY")
            .build();
		default -> throw new IllegalArgumentException("Unexpected value: " + action);
        };
    }
}