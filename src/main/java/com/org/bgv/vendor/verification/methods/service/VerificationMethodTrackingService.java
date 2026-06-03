package com.org.bgv.vendor.verification.methods.service;


import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.enums.VerificationExecutionAction;
import com.org.bgv.vendor.dto.ExecutionActionDto;
import com.org.bgv.vendor.dto.VerificationMethodExecutionDetailsDto;
import com.org.bgv.vendor.dto.VerificationMethodFieldDTO;
import com.org.bgv.vendor.entity.VerificationMethodExecution;
import com.org.bgv.vendor.entity.VerificationMethodExecutionField;
import com.org.bgv.vendor.entity.VerificationMethodField;
import com.org.bgv.vendor.repository.VerificationMethodExecutionFieldRepository;
import com.org.bgv.vendor.repository.VerificationMethodExecutionRepository;
import com.org.bgv.vendor.repository.VerificationMethodFieldRepository;
import com.org.bgv.vendor.service.ExecutionActionConfigService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationMethodTrackingService {

    private final VerificationMethodExecutionRepository executionRepository;

    private final VerificationMethodExecutionFieldRepository fieldRepository;
    private final VerificationMethodFieldRepository methodFieldRepository;
    private final ExecutionActionConfigService executionActionConfigService;

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

    private VerificationMethodExecutionDetailsDto
    mapExecution(
            VerificationMethodExecution execution) {

        List<VerificationMethodExecutionField> values =
                fieldRepository.findByExecutionExecutionId(
                        execution.getExecutionId());

        List<VerificationMethodFieldDTO> fields =
                values.stream()
                        .map(field ->
                        VerificationMethodFieldDTO.builder()
                                        .fieldName(field.getFieldName())
                                        .fieldLabel(
                                                field.getFieldName())
                                        .fieldType("TEXT")
                                        .value(field.getFieldValue())
                                        .build())
                        .toList();

        return VerificationMethodExecutionDetailsDto
                .builder()
                .executionId(
                        execution.getExecutionId())
                .methodCode(
                        execution.getVerificationMethod()
                                .getCode())
                .methodName(
                        execution.getVerificationMethod()
                                .getName())
                .status(
                        execution.getStatus())
                .initiatedAt(
                        execution.getInitiatedAt())
                .completedAt(
                        execution.getCompletedAt())
                .fields(fields)
                .build();
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
                	    executionActionConfigService.getActions(execution.getStatus())
                	        .stream()
                	        .map(this::mapAction)
                	        .toList()
                	)
                .build();
    }
    
    private ExecutionActionDto mapAction(VerificationExecutionAction action) {

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
        };
    }
}