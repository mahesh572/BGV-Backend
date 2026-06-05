package com.org.bgv.vendor.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.org.bgv.enums.VerificationExecutionAction;
import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.enums.VerificationMethodCode;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerificationMethodExecutionDetailsDto {

    private Long executionId;

    private VerificationMethodCode methodCode;
    
    private Long checkId;

    private String methodName;

    private VerificationExecutionStatus status;

    private LocalDateTime initiatedAt;

    private LocalDateTime completedAt;

    private List<VerificationMethodFieldDTO> fields;
    private List<ExecutionActionDto> allowedActions;
    
    private FieldVisitAssignmentDto fieldVisitAssignment;
    
 // Notes
    private List<VerificationExecutionNoteDto> notes;
}