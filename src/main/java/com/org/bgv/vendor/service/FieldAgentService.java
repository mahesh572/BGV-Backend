package com.org.bgv.vendor.service;


import com.org.bgv.config.SecurityUtils;
import com.org.bgv.entity.User;
import com.org.bgv.enums.VerificationExecutionAction;
import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.service.UserService;
import com.org.bgv.vendor.dto.CompleteFieldVisitRequest;
import com.org.bgv.vendor.dto.ExecutionActionDto;
import com.org.bgv.vendor.dto.FieldAssignmentDTO;
import com.org.bgv.vendor.dto.UpdateExecutionStatusRequest;
import com.org.bgv.vendor.entity.FieldVisitAssignment;
import com.org.bgv.vendor.entity.FieldVisitLocation;
import com.org.bgv.vendor.entity.VerificationMethodExecution;
import com.org.bgv.vendor.enums.LocationSource;
import com.org.bgv.vendor.enums.VisitLocationType;
import com.org.bgv.vendor.repository.FieldVisitAssignmentRepository;
import com.org.bgv.vendor.repository.FieldVisitLocationRepository;
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
import java.util.Objects;
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
 private final FieldVisitLocationRepository fieldVisitLocationRepository;

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
    
   
     return assignments.stream()
         .map(this::convertToDTO)
         .filter(Objects::nonNull)
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
 public FieldAssignmentDTO startVisit(
         Long executionId,
         UpdateExecutionStatusRequest request) {
	 
	 User fieldAgent = userService.getUserById(SecurityUtils.getCurrentUserId());
	 
	 FieldVisitAssignment assignment =
             assignmentRepository.findByExecutionExecutionId(executionId)
             .orElseThrow(() -> new RuntimeException("Assignment not found"));

     // Verify ownership
     if (!assignment.getFieldAgent().getUserId().equals(fieldAgent.getUserId())) {
         throw new RuntimeException("Unauthorized access to assignment");
     }

     VerificationMethodExecution execution = assignment.getExecution();

     // Validate current execution status
     VerificationExecutionStatus currentStatus = execution.getStatus();
/*
     if (currentStatus != VerificationExecutionStatus.VISIT_ASSIGNED
             && currentStatus != VerificationExecutionStatus.INITIATED) {

         throw new RuntimeException(
                 "Cannot start visit from current status : " + currentStatus);
     }
*/
     LocalDateTime now = LocalDateTime.now();

     /*
      * Update Assignment
      */
     assignment.setStartedAt(now);

     FieldVisitAssignment savedAssignment =
             assignmentRepository.save(assignment);

     /*
      * Capture Start Location
      */
     FieldVisitLocation location = FieldVisitLocation.builder()
             .assignment(savedAssignment)
             .latitude(request.getLatitude())
             .longitude(request.getLongitude())
             .accuracy(request.getAccuracy())
             .address(request.getAddress())
             .visitType(VisitLocationType.START)
             .source(LocationSource.GPS)
             .capturedAt(now)
             .capturedBy(fieldAgent.getUserId())
             .build();

     fieldVisitLocationRepository.save(location);

     /*
      * Update Execution
      */
     execution.setStatus(VerificationExecutionStatus.VISIT_IN_PROGRESS);

     if (request.getNotes() != null && !request.getNotes().isBlank()) {
         execution.setOutcomeRemarks(request.getNotes());
     }

     executionRepository.save(execution);

     /*
      * Return latest assignment DTO
      */
     return convertToDTO(savedAssignment);
 }

 /**
  * Complete a field visit
  */
 @Transactional
 public FieldAssignmentDTO completeVisit(
         Long executionId,
         CompleteFieldVisitRequest request) {

     FieldVisitAssignment assignment =
             assignmentRepository.findByExecutionExecutionId(executionId)
             .orElseThrow(() -> new RuntimeException("Assignment not found"));

     assignment.setOutcome(request.getOutcome());
     assignment.setRemarks(request.getRemarks());
    // assignment.setLatitude(request.getLatitude());
    // assignment.setLongitude(request.getLongitude());
     assignment.setCompletedAt(LocalDateTime.now());

     VerificationMethodExecution execution = assignment.getExecution();
    // execution.setStatus(VerificationExecutionStatus.VISIT_COMPLETED);
     execution.setStatus(VerificationExecutionStatus.PENDING_REVIEW);
     execution.setOutcomeCode(request.getOutcome());
     execution.setOutcomeRemarks(request.getRemarks());
     execution.setCompletedAt(LocalDateTime.now());

     executionRepository.save(execution);
     
     FieldVisitLocation location = FieldVisitLocation.builder()
    	        .assignment(assignment)
    	        .latitude(request.getLatitude())
    	        .longitude(request.getLongitude())
    	        .address(request.getAddress())
    	       // .accuracy(request.getAccuracy())
    	        .visitType(VisitLocationType.COMPLETION)
    	        .capturedAt(LocalDateTime.now())
    	        .capturedBy(SecurityUtils.getCurrentUserId())
    	        .source(LocationSource.GPS)
    	        .build();

    	fieldVisitLocationRepository.save(location);

     return convertToDTO(
             assignmentRepository.save(assignment));
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
     
   //  assignment.setLatitude(latitude);
   //  assignment.setLongitude(longitude);
   //  assignment.setLocationCapturedAt(LocalDateTime.now());
   //  assignment.setLocationUpdatedBy(fieldAgent.getUserId());
     
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
     
   //  boolean canStart = execution.getStatus() == VerificationExecutionStatus.VISIT_ASSIGNED || execution.getStatus() == VerificationExecutionStatus.INITIATED;
     
    // boolean canComplete = execution.getStatus() == VerificationExecutionStatus.VISIT_IN_PROGRESS;
     
     if(execution.getVerificationCheck()==null) {
    	 return null;
     }
     
    /* 
     FieldVisitLocation startLocation =
    		    repository.findByAssignmentAndVisitType(
    		        assignment,
    		        VisitLocationType.START)
    		    .orElse(null);

    		FieldVisitLocation completionLocation =
    		    repository.findByAssignmentAndVisitType(
    		        assignment,
    		        VisitLocationType.COMPLETION)
    		    .orElse(null);
     
     */
     FieldVisitLocation latestLocation =
    	        fieldVisitLocationRepository
    	            .findTopByAssignmentAssignmentIdOrderByCapturedAtDesc(assignment.getAssignmentId())
    	            .orElse(null);
     
     log.info("execution::::::::::::::::::::::::::::{}",execution.toString());
     
     return FieldAssignmentDTO.builder()
    		 .checkId(execution.getVerificationCheck().getCaseCheckId())
         .assignmentId(assignment.getAssignmentId())
         .executionId(execution.getExecutionId())
         .verificationMethodName(execution.getVerificationMethod().getName())
         .verificationMethodCode(execution.getVerificationMethod().getCode().name())
         .status(execution.getStatus().name())
         .visitAddress(assignment.getVisitAddress())
         .latitude(latestLocation != null ? latestLocation.getLatitude() : null)
         .longitude(latestLocation != null ? latestLocation.getLongitude() : null)
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