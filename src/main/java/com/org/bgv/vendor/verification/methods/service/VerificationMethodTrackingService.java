package com.org.bgv.vendor.verification.methods.service;


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.enums.VerificationExecutionAction;
import com.org.bgv.enums.VerificationMethodCode;
import com.org.bgv.enums.VerificationOutcome;
import com.org.bgv.vendor.dto.ExecutionActionDto;
import com.org.bgv.vendor.dto.FieldVisitDto;
import com.org.bgv.vendor.dto.VerificationExecutionNoteDto;
import com.org.bgv.vendor.dto.VerificationMethodExecutionDetailsDto;
import com.org.bgv.vendor.dto.VerificationMethodFieldDTO;
import com.org.bgv.vendor.dto.VerificationOutcomeDTO;
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
    
    public List<VerificationMethodExecutionDetailsDto> getExecutions(
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
        
        FieldVisitDto assignmentDto = null;

        if (VerificationMethodCode.FIELD_VISIT.equals(
                execution.getVerificationMethod().getCode())) {

            assignmentDto =
                    fieldVisitAssignmentRepository
                            .findByExecutionExecutionId(
                                    execution.getExecutionId())
                            .map(assignment ->

                            FieldVisitDto.builder()
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
    
    public ExecutionActionDto mapAction(VerificationExecutionAction action) {
    	
    	
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
		            .outcomes(getPhoneCallOutcomes())
		            .build();
            case MARK_PHONE_CALL_COMPLETED -> ExecutionActionDto.builder()
		            .action(action.name())
		            .label("Mark Phone Call Completed")
		            .type("SUCCESS")
		            .outcomes(getVerificationOutcomes())
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
                    .outcomes(getVerificationOutcomes())
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
            case APPROVE -> ExecutionActionDto.builder()
		            .action(action.name())
		            .label("Approve")
		            .type("PRIMARY")
		            .build();
            case ESCALATE -> ExecutionActionDto.builder()
		            .action(action.name())
		            .label("Escalate")
		            .type("PRIMARY")
		            .build();
            case REWORK -> ExecutionActionDto.builder()
		            .action(action.name())
		            .label("Rework")
		            .type("PRIMARY")
		            .build();
            case UNDER_REVIEW -> ExecutionActionDto.builder()
		            .action(action.name())
		            .label("Under Review")
		            .type("PRIMARY")
		            .build();
            case SEND_EMAIL -> ExecutionActionDto.builder()
		            .action(action.name())
		            .label("Send Email")
		            .type("PRIMARY")
		            .build();
            case RESEND_EMAIL -> ExecutionActionDto.builder()
		            .action(action.name())
		            .label("ReSend Email")
		            .type("PRIMARY")
		            .build();
            case MARK_EMAIL_VERIFICATION_COMPLETED -> ExecutionActionDto.builder()
		            .action(action.name())
		            .label("Complete Email Verification")
		            .outcomes(getVerificationOutcomes())
		            .type("PRIMARY")
		            .build();
            
		default -> throw new IllegalArgumentException("Unexpected value: " + action);
        };
    }
    
    
    
    private List<VerificationOutcomeDTO> getPhoneCallOutcomes() {

        return List.of(

                VerificationOutcomeDTO.builder()
                        .code("CONNECTED_SUCCESS")
                        .label("Connected - Successful")
                        .description("Verification completed successfully over the call")
                        .success(true)
                      //  .canComplete(true)
                      //  .nextStatus("PHONE_CALL_COMPLETED")
                        .evidenceRequired(false)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("CONNECTED_PARTIAL")
                        .label("Connected - Partial Information")
                        .description("Partial information received")
                        .success(true)
                       // .canComplete(true)
                      //  .nextStatus("PHONE_CALL_COMPLETED")
                        .evidenceRequired(false)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("CONNECTED_FOLLOWUP")
                        .label("Connected - Follow-up Required")
                        .description("Additional follow-up is required")
                        .success(true)
                      //  .canComplete(false)
                      //  .nextStatus("FOLLOW_UP_REQUIRED")
                        .evidenceRequired(false)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("NOT_ANSWERED")
                        .label("Not Answered")
                        .description("Call was not answered")
                        .success(false)
                       // .canComplete(false)
                      //  .nextStatus("PHONE_CALL_INITIATED")
                        .evidenceRequired(false)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("WRONG_NUMBER")
                        .label("Wrong Number")
                        .description("Provided phone number is incorrect")
                        .success(false)
                      //  .canComplete(false)
                      //  .nextStatus("CONTACT_DETAILS_INVALID")
                        .evidenceRequired(false)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("CALL_REJECTED")
                        .label("Call Rejected")
                        .description("Recipient rejected the call")
                        .success(false)
                      //  .canComplete(false)
                       // .nextStatus("PHONE_CALL_REJECTED")
                        .evidenceRequired(false)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("CALLBACK_REQUESTED")
                        .label("Callback Requested")
                        .description("Recipient requested a callback")
                        .success(false)
                      //  .canComplete(false)
                       // .nextStatus("CALLBACK_SCHEDULED")
                        .evidenceRequired(false)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("NUMBER_DISCONNECTED")
                        .label("Number Disconnected")
                        .description("Phone number is disconnected")
                        .success(false)
                      //  .canComplete(false)
                      //  .nextStatus("CONTACT_DETAILS_INVALID")
                        .evidenceRequired(false)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("VOICEMAIL_LEFT")
                        .label("Voicemail Left")
                        .description("Voicemail message left")
                        .success(false)
                      //  .canComplete(false)
                      //  .nextStatus("WAITING_FOR_CALLBACK")
                        .evidenceRequired(false)
                        .remarksRequired(false)
                        .build()
        );
    }
    
    
    private List<VerificationOutcomeDTO> getFieldVisitOutcomes() {

        return List.of(

                VerificationOutcomeDTO.builder()
                        .code("COMPLETED_SUCCESSFULLY")
                        .label("Completed Successfully")
                        .description("Field verification completed successfully")
                        .success(true)
                      //  .canComplete(true)
                       // .nextStatus("FIELD_VISIT_COMPLETED")
                        .evidenceRequired(true)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("PARTIAL_COMPLETION")
                        .label("Partial Completion")
                        .description("Field visit completed with partial information")
                        .success(true)
                      //  .canComplete(true)
                      //  .nextStatus("FIELD_VISIT_COMPLETED")
                        .evidenceRequired(true)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("CANDIDATE_NOT_AVAILABLE")
                        .label("Candidate Not Available")
                        .description("Candidate was not available at the location")
                        .success(false)
                      //  .canComplete(false)
                      //  .nextStatus("FIELD_VISIT_PENDING")
                        .evidenceRequired(false)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("ADDRESS_NOT_FOUND")
                        .label("Address Not Found")
                        .description("Provided address could not be located")
                        .success(false)
                       // .canComplete(false)
                      //  .nextStatus("ADDRESS_VERIFICATION_REQUIRED")
                        .evidenceRequired(true)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("DOCUMENTS_NOT_READY")
                        .label("Documents Not Ready")
                        .description("Required documents were not available")
                        .success(false)
                       // .canComplete(false)
                       // .nextStatus("FOLLOW_UP_REQUIRED")
                        .evidenceRequired(false)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("RESCHEDULE_REQUESTED")
                        .label("Reschedule Requested")
                        .description("Visit has been requested to be rescheduled")
                        .success(false)
                      //  .canComplete(false)
                      //  .nextStatus("FIELD_VISIT_RESCHEDULED")
                        .evidenceRequired(false)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("WRONG_ADDRESS")
                        .label("Wrong Address Provided")
                        .description("Provided address is incorrect")
                        .success(false)
                      //  .canComplete(false)
                       // .nextStatus("ADDRESS_VERIFICATION_REQUIRED")
                        .evidenceRequired(true)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("CONTACT_NOT_REACHABLE")
                        .label("Contact Not Reachable")
                        .description("Unable to reach the contact person")
                        .success(false)
                      //  .canComplete(false)
                       // .nextStatus("FOLLOW_UP_REQUIRED")
                        .evidenceRequired(false)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("BUSINESS_CLOSED")
                        .label("Business Closed / Permanently Shut")
                        .description("Business location was found closed or permanently shut")
                        .success(false)
                      //  .canComplete(false)
                       // .nextStatus("BUSINESS_NOT_OPERATIONAL")
                        .evidenceRequired(true)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("SECURITY_DENIED_ACCESS")
                        .label("Security Denied Access")
                        .description("Security personnel denied access to the premises")
                        .success(false)
                      //  .canComplete(false)
                      //  .nextStatus("ACCESS_DENIED")
                        .evidenceRequired(false)
                        .remarksRequired(true)
                        .build(),

                VerificationOutcomeDTO.builder()
                        .code("NO_RESPONSE")
                        .label("No Response at Location")
                        .description("No one responded at the location during the visit")
                        .success(false)
                      //  .canComplete(false)
                       // .nextStatus("FIELD_VISIT_PENDING")
                        .evidenceRequired(false)
                        .remarksRequired(true)
                        .build()
        );
    }
    
    
    private List<VerificationOutcomeDTO> getVerificationOutcomes() {

        return Arrays.stream(VerificationOutcome.values())
                .map(outcome -> VerificationOutcomeDTO.builder()
                        .code(outcome.name())
                        .label(outcome.getLabel())
                        .description(outcome.getDescription())
                        .success(outcome.isSuccess())
                        .evidenceRequired(outcome.isEvidenceRequired())
                        .remarksRequired(outcome.isRemarksRequired())
                        .build())
                .toList();
    }
}