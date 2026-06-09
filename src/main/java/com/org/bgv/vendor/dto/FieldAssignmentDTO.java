package com.org.bgv.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldAssignmentDTO {
 private Long assignmentId;
 private Long executionId;
 private String verificationMethodName;
 private String verificationMethodCode;
 private String status;
 private String visitAddress;
 private Double latitude;
 private Double longitude;
 private LocalDate scheduledDate;
 private LocalDateTime assignedAt;
 private LocalDateTime startedAt;
 private LocalDateTime completedAt;
 private String remarks;
 private String contactName;
 private String contactPhone;
 private String contactEmail;
 private String organization;
 private String designation;
 private String additionalInstructions;
 private boolean isOverdue;
 private boolean canStart;
 private boolean canComplete;
 private List<ExecutionActionDto> allowedActions;
 
 
}
