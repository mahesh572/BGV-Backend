package com.org.bgv.vendor.service;


import com.org.bgv.config.SecurityUtils;
import com.org.bgv.entity.User;
import com.org.bgv.enums.VerificationExecutionAction;
import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.service.UserService;
import com.org.bgv.vendor.dto.ExecutionActionDto;
import com.org.bgv.vendor.dto.FieldAssignmentDTO;
import com.org.bgv.vendor.entity.FieldVisitAssignment;
import com.org.bgv.vendor.entity.VerificationMethodExecution;
import com.org.bgv.vendor.repository.FieldVisitAssignmentRepository;
import com.org.bgv.vendor.repository.VerificationMethodExecutionRepository;
import com.org.bgv.vendor.verification.methods.service.VerificationMethodTrackingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FieldAgentService {

 private final FieldVisitAssignmentRepository assignmentRepository;
 private final VerificationMethodExecutionRepository executionRepository;
 private final UserService userService;
 private final ExecutionActionConfigService executionActionConfigService;
 private final VerificationMethodTrackingService  verificationMethodTrackingService;

 /**
  * Get dashboard summary for field agent
  */
 public Map<String, Object> getDashboardSummary() {
     
	 
	 Long userId = SecurityUtils.getCurrentUserId();
	 User fieldAgent = userService.getUserById(userId);
	 
	 LocalDate today = LocalDate.now();
     
     List<FieldVisitAssignment> upcomingAssignments = assignmentRepository.findUpcomingAssignments(fieldAgent, today);
     List<FieldVisitAssignment> overdueAssignments = assignmentRepository.findOverdueAssignments(fieldAgent, today);
     List<FieldVisitAssignment> todayAssignments = assignmentRepository.findTodayAssignments(fieldAgent, today);
     List<FieldVisitAssignment> activeAssignments = assignmentRepository.findActiveAssignments(fieldAgent);
     
     // Count by status
     List<Object[]> statusCounts = assignmentRepository.countAssignmentsByStatus(fieldAgent);
     Map<String, Long> statusCountMap = new HashMap<>();
     for (Object[] statusCount : statusCounts) {
         statusCountMap.put(((VerificationExecutionStatus) statusCount[0]).name(), (Long) statusCount[1]);
     }
     
     Map<String, Object> dashboard = new HashMap<>();
     dashboard.put("totalActive", activeAssignments.size());
     dashboard.put("upcoming", upcomingAssignments.size());
     dashboard.put("overdue", overdueAssignments.size());
     dashboard.put("today", todayAssignments.size());
     dashboard.put("statusBreakdown", statusCountMap);
     
     return dashboard;
 }

 /**
  * Get all assignments for field agent
  */
 public List<FieldAssignmentDTO> getMyAssignments() {
     List<FieldVisitAssignment> assignments;
     
     Long userId = SecurityUtils.getCurrentUserId();
    User fieldAgent = userService.getUserById(userId);
    assignments = assignmentRepository.findByFieldAgent(fieldAgent);
    
    /*
    if (status != null && !status.isEmpty()) {
         VerificationExecutionStatus executionStatus = VerificationExecutionStatus.valueOf(status);
         assignments = assignmentRepository.findByFieldAgentAndStatus(fieldAgent, executionStatus);
     } else if (date != null) {
         assignments = assignmentRepository.findByFieldAgentAndScheduledDateBetween(fieldAgent, date, date);
     } else {
         
     }
     */
     return assignments.stream()
         .map(this::convertToDTO)
         .collect(Collectors.toList());
 }

 /**
  * Get assignment details by ID
  */
 public FieldAssignmentDTO getAssignmentDetails(
	        Long assignmentId
	        ) {
	 
	    Long userId = SecurityUtils.getCurrentUserId();
	    User fieldAgent = userService.getUserById(userId);

	    FieldVisitAssignment assignment =
	            assignmentRepository.findById(assignmentId)
	                    .orElseThrow(() ->
	                            new RuntimeException("Assignment not found"));

	    VerificationMethodExecution execution =
	            assignment.getExecution();

	    List<ExecutionActionDto> executionActionDtos =
	            executionActionConfigService
	                    .getActions(
	                            execution.getVerificationMethod()
	                                     .getCode(),
	                            execution.getStatus())
	                    .stream()
	                    .map(verificationMethodTrackingService::mapAction)
	                    .toList();

	    if (!assignment.getFieldAgent()
	            .getUserId()
	            .equals(fieldAgent.getUserId())) {

	        throw new RuntimeException(
	                "Unauthorized access to assignment");
	    }

	    return convertToDTO(
	            assignment,
	            executionActionDtos);
	}

 /**
  * Start a field visit
  */
 @Transactional
 public FieldAssignmentDTO startVisit(Long assignmentId, User fieldAgent, Double latitude, Double longitude, String address) {
     FieldVisitAssignment assignment = assignmentRepository.findById(assignmentId)
         .orElseThrow(() -> new RuntimeException("Assignment not found"));
     
     // Verify ownership
     if (!assignment.getFieldAgent().getUserId().equals(fieldAgent.getUserId())) {
         throw new RuntimeException("Unauthorized access to assignment");
     }
     
     // Validate status
     VerificationExecutionStatus currentStatus = assignment.getExecution().getStatus();
     if (currentStatus != VerificationExecutionStatus.VISIT_ASSIGNED && 
         currentStatus != VerificationExecutionStatus.INITIATED) {
         throw new RuntimeException("Cannot start visit from current status: " + currentStatus);
     }
     
     // Update assignment
     assignment.setStartedAt(LocalDateTime.now());
     assignment.setLatitude(latitude);
     assignment.setLongitude(longitude);
     
     // Update execution status
     VerificationMethodExecution execution = assignment.getExecution();
     execution.setStatus(VerificationExecutionStatus.VISIT_IN_PROGRESS);
     executionRepository.save(execution);
     
     FieldVisitAssignment savedAssignment = assignmentRepository.save(assignment);
     return convertToDTO(savedAssignment);
 }

 /**
  * Complete a field visit
  */
 @Transactional
 public FieldAssignmentDTO completeVisit(Long assignmentId, User fieldAgent, String outcome, String remarks,
                                         Double latitude, Double longitude, String address) {
     FieldVisitAssignment assignment = assignmentRepository.findById(assignmentId)
         .orElseThrow(() -> new RuntimeException("Assignment not found"));
     
     // Verify ownership
     if (!assignment.getFieldAgent().getUserId().equals(fieldAgent.getUserId())) {
         throw new RuntimeException("Unauthorized access to assignment");
     }
     
     // Validate status
     if (assignment.getExecution().getStatus() != VerificationExecutionStatus.VISIT_IN_PROGRESS) {
         throw new RuntimeException("Cannot complete visit from current status");
     }
     
     // Update assignment
     assignment.setCompletedAt(LocalDateTime.now());
     assignment.setOutcome(outcome);
     if (remarks != null) {
         assignment.setRemarks(remarks);
     }
     if (latitude != null && longitude != null) {
         assignment.setLatitude(latitude);
         assignment.setLongitude(longitude);
     }
     
     // Update execution status
     VerificationMethodExecution execution = assignment.getExecution();
     execution.setStatus(VerificationExecutionStatus.COMPLETED);
     execution.setCompletedAt(LocalDateTime.now());
     executionRepository.save(execution);
     
     FieldVisitAssignment savedAssignment = assignmentRepository.save(assignment);
     return convertToDTO(savedAssignment);
 }

 /**
  * Update location during visit
  */
 @Transactional
 public void updateLocation(Long assignmentId, User fieldAgent, Double latitude, Double longitude, String address) {
     FieldVisitAssignment assignment = assignmentRepository.findById(assignmentId)
         .orElseThrow(() -> new RuntimeException("Assignment not found"));
     
     if (!assignment.getFieldAgent().getUserId().equals(fieldAgent.getUserId())) {
         throw new RuntimeException("Unauthorized access to assignment");
     }
     
     assignment.setLatitude(latitude);
     assignment.setLongitude(longitude);
     assignment.setLocationCapturedAt(LocalDateTime.now());
     assignment.setLocationUpdatedBy(fieldAgent.getUserId());
     
     assignmentRepository.save(assignment);
 }

 /**
  * Convert entity to DTO
  */
 private FieldAssignmentDTO convertToDTO(FieldVisitAssignment assignment, List<ExecutionActionDto> allowedActionList) {
     VerificationMethodExecution execution = assignment.getExecution();
     User fieldAgent = assignment.getFieldAgent();
     
     // Extract contact information from execution fields
     // This depends on how you store field values in your system
     Map<String, String> contactInfo = extractContactInfo(execution);
     
     LocalDate today = LocalDate.now();
     boolean isOverdue = assignment.getScheduledDate() != null && 
                        assignment.getScheduledDate().isBefore(today) &&
                        execution.getStatus() != VerificationExecutionStatus.COMPLETED;
     
     boolean canStart = execution.getStatus() == VerificationExecutionStatus.VISIT_ASSIGNED ||
                       execution.getStatus() == VerificationExecutionStatus.INITIATED;
     
     boolean canComplete = execution.getStatus() == VerificationExecutionStatus.VISIT_IN_PROGRESS;
     
     return FieldAssignmentDTO.builder()
         .assignmentId(assignment.getAssignmentId())
         .executionId(execution.getExecutionId())
         .verificationMethodName(execution.getVerificationMethod().getName())
         .verificationMethodCode(execution.getVerificationMethod().getCode().name())
         .status(execution.getStatus().name())
         .visitAddress(assignment.getVisitAddress())
         .latitude(assignment.getLatitude())
         .longitude(assignment.getLongitude())
         .scheduledDate(assignment.getScheduledDate())
         .assignedAt(assignment.getAssignedAt())
         .startedAt(assignment.getStartedAt())
         .completedAt(assignment.getCompletedAt())
         .remarks(assignment.getRemarks())
         .contactName(contactInfo.get("contactName"))
         .contactPhone(contactInfo.get("contactPhone"))
         .contactEmail(contactInfo.get("contactEmail"))
         .organization(contactInfo.get("organization"))
         .designation(contactInfo.get("designation"))
         .additionalInstructions(contactInfo.get("additionalInstructions"))
         .isOverdue(isOverdue)
         .canStart(canStart)
         .canComplete(canComplete)
         .allowedActions(allowedActionList)
         .build();
 }
 
 private Map<String, String> extractContactInfo(VerificationMethodExecution execution) {
     // Implement based on how you store field values
     // This is a placeholder - adjust according to your data structure
     Map<String, String> contactInfo = new HashMap<>();
     
     // Example: If you have a VerificationCheck with fields
     if (execution.getVerificationCheck() != null) {
         // Extract from verification check fields
         // contactInfo.put("contactName", execution.getVerificationCheck().getContactName());
         // etc.
     }
     
     return contactInfo;
 }
 
 
 private FieldAssignmentDTO convertToDTO(
	        FieldVisitAssignment assignment) {

	    VerificationMethodExecution execution =
	            assignment.getExecution();

	    List<ExecutionActionDto> actions =
	            executionActionConfigService
	                    .getActions(
	                            execution.getVerificationMethod().getCode(),
	                            execution.getStatus())
	                    .stream()
	                    .map(verificationMethodTrackingService::mapAction)
	                    .toList();

	    return convertToDTO(
	            assignment,
	            actions);
	}
}