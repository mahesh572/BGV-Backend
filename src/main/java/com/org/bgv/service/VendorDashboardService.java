package com.org.bgv.service;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.common.RoleConstants;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.entity.*;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.repository.*;
import com.org.bgv.service.util.UserServiceUtil;
import com.org.bgv.vendor.dto.ActiveCaseDto;
import com.org.bgv.vendor.dto.CheckSummaryDto;
import com.org.bgv.vendor.dto.VendorDashboardResponse;
import com.org.bgv.vendor.dto.VerificationStatsDto;
import com.org.bgv.vendor.dto.WorkloadDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorDashboardService {
    
    private final VerificationCaseRepository verificationCaseRepository;
    private final VerificationCaseCheckRepository verificationCaseCheckRepository;
    private final CheckCategoryRepository checkCategoryRepository;
    private final CompanyRepository companyRepository;
    private final CandidateRepository candidateRepository;
    private final UserServiceUtil userServiceUtil;
    
    
    private static final int DEFAULT_SLA_DAYS = 14;
    
   
    @Transactional(readOnly = true)
    public VendorDashboardResponse getVendorDashboardData(Long vendorUserId) {
    	
    	Boolean isVendorAdmin = userServiceUtil.hasRole(vendorUserId, RoleConstants.ROLE_VENDOR_ADMINISTRATOR);
    	
    	log.info("isVendorAdmin:::::::::::::::::::::{}",isVendorAdmin);
    	 List<VerificationCaseCheck> checks = new ArrayList<>();
    	if(isVendorAdmin) {
    		checks = verificationCaseCheckRepository.findByVendorCompany_IdOrderByUpdatedAtDesc(SecurityUtils.getCurrentUserCompanyId());
    	}else {
    		checks = verificationCaseCheckRepository.findByAssignedVendorUser_UserId(vendorUserId);
    	}
    	
        
     // Extract unique cases from checks
        List<VerificationCase> vendorCases = checks.stream()
            .map(VerificationCaseCheck::getVerificationCase)
            .distinct()
            .collect(Collectors.toList());

        return VendorDashboardResponse.builder()
                .workload(getWorkloadData(checks))
                .activeCases(getActiveCases(vendorCases))
              //  .verificationQueue(getVerificationQueue(checks))
                .verificationStats(getVerificationStats(checks))
              //  .recentVerifications(getRecentVerifications(checks))
                .build();
    }
    
    private WorkloadDto getWorkloadData(List<VerificationCaseCheck> checks) {

        long totalAssigned = checks.size();

        long pending = checks.stream()
                .filter(c -> c.getStatus() == CaseCheckStatus.PENDING)
                .count();

        long inProgress = checks.stream()
                .filter(c -> c.getStatus() == CaseCheckStatus.IN_PROGRESS)
                .count();

        long completed = checks.stream()
                .filter(c -> c.getStatus() == CaseCheckStatus.COMPLETED)
                .count();

        return WorkloadDto.builder()
                .totalAssigned(totalAssigned)
                .pending(pending)
                .inProgress(inProgress)
                .completed(completed)
                .build();
    }
    
    private List<VerificationStatsDto> getVerificationStats(
            List<VerificationCaseCheck> checks) {

        return checks.stream()
                .collect(Collectors.groupingBy(
                        check -> check.getCategory()
                                      .getName()))
                .entrySet()
                .stream()
                .map(entry -> {

                    List<VerificationCaseCheck> categoryChecks = entry.getValue();

                    return VerificationStatsDto.builder()
                            .checkType(entry.getKey())
                            .assigned(categoryChecks.size())
                            .pending(categoryChecks.stream()
                                    .filter(c -> c.getStatus() == CaseCheckStatus.PENDING)
                                    .count())
                            .inProgress(categoryChecks.stream()
                                    .filter(c -> c.getStatus() == CaseCheckStatus.IN_PROGRESS)
                                    .count())
                            .completed(categoryChecks.stream()
                                    .filter(c -> c.getStatus() == CaseCheckStatus.COMPLETED)
                                    .count())
                            .build();
                })
                .toList();
    }
    
    
    private Map<String, Object> getCategoryStats(List<VerificationCase> cases, CheckCategory category) {
        // Get all checks for this category across all cases
        List<VerificationCaseCheck> categoryChecks = new ArrayList<>();
        for (VerificationCase verificationCase : cases) {
            List<VerificationCaseCheck> checks = verificationCase.getCaseChecks().stream()
                .filter(check -> check.getCategory() != null && 
                                 check.getCategory().getCategoryId().equals(category.getCategoryId()))
                .collect(Collectors.toList());
            categoryChecks.addAll(checks);
        }
        
        long assigned = categoryChecks.size();
        long completed = categoryChecks.stream()
            .filter(check -> check.getStatus() == CaseCheckStatus.COMPLETED)
            .count();
        long inProgress = categoryChecks.stream()
            .filter(check -> check.getStatus() == CaseCheckStatus.PENDING)
            .count();
        long pending = categoryChecks.stream()
            .filter(check -> check.getStatus() == CaseCheckStatus.PENDING)
            .count();
        
        return Map.of(
            "assigned", assigned,
            "completed", completed,
            "inProgress", inProgress,
            "pending", pending
        );
    }
    
    private List<ActiveCaseDto> getActiveCases(List<VerificationCase> cases) {

        return cases.stream()
                .filter(c -> c.getStatus() != CaseStatus.COMPLETED)
                .map(verificationCase -> {
                	
                	Candidate candidate =
                            candidateRepository
                                    .findById(verificationCase.getCandidateId())
                                    .orElse(null);
                	
                	Company company =
                            companyRepository
                                    .findById(verificationCase.getCompanyId())
                                    .orElse(null);
                	
                	List<CheckSummaryDto> checks =
                            verificationCase.getCaseChecks()
                                    .stream()
                                    .map(check -> CheckSummaryDto.builder()
                                            .checkId(check.getCaseCheckId())
                                            .checkRef(check.getCheckRef())
                                            .checkType(check.getCategory() != null ? check.getCategory().getCode().toLowerCase(): "unknown")
                                           // .status(getStatusMapping(check.getStatus()))
                                            .status(check.getStatus().name())
                                            .slaStatus(calculateSlaStatus(check))
                                            .build())
                                    .toList();
                	
                	return ActiveCaseDto.builder()
                	.caseId(verificationCase.getCaseId())
                	.caseRef(
                            verificationCase.getCaseNumber() != null
                                    ? verificationCase.getCaseNumber()
                                    : "")
                	.candidateId(
                            candidate != null
                                    ? String.valueOf(candidate.getCandidateId())
                                    : "")
                	.candidateName(
                            candidate != null
                                    ? candidate.getFirstName() + " " + candidate.getLastName()
                                    : "N/A")
                	.employerName(
                            company != null
                                    ? company.getCompanyName()
                                    : "Unknown Company")
                	 .checks(checks)
                	 .slaStatus(
                             calculateOverallSlaStatus(
                                     verificationCase,
                                     checks))
                	 .daysRemaining(
                             calculateDaysRemaining(
                                     verificationCase))
                	 .priority(
                             determinePriority(
                                     verificationCase,
                                     checks))
                	
                	.build();
                	
                	
                })
                .toList();
    }
    private List<CheckSummaryDto> getCheckSummaries(
            VerificationCase verificationCase) {

        return verificationCase.getCaseChecks()
                .stream()
                .map(check -> CheckSummaryDto.builder()
                        .checkId(check.getCaseCheckId())
                        .checkRef(check.getCheckRef())
                        .type(check.getCategory().getName())
                        .status(getStatusMapping(check.getStatus()))
                        .slaStatus(calculateSlaStatus(check))
                        .build())
                .toList();
    }
    
    private List<Map<String, String>> getVerificationQueue(List<VerificationCaseCheck> checks) {
        return checks.stream()
            .filter(check -> check.getStatus() == CaseCheckStatus.PENDING || 
                            check.getStatus() == CaseCheckStatus.PENDING)
            .map(check -> {
                String priority = determineCheckPriority(check);
                String waitTime = calculateWaitTime(check);
                
                return Map.of(
                    "id", "VQ-" + check.getCaseCheckId(),
                    "checkType", check.getCategory() != null ? 
                        check.getCategory().getCode().toLowerCase() : "unknown",
                    "caseId", "CASE-" + check.getVerificationCase().getCaseId(),
                    "priority", priority,
                    "waitTime", waitTime
                );
            })
            .sorted((a, b) -> {
                int priorityCompare = getPriorityValue((String) b.get("priority")) - 
                                    getPriorityValue((String) a.get("priority"));
                if (priorityCompare != 0) return priorityCompare;
                
                return extractWaitTimeMinutes((String) a.get("waitTime")) - 
                       extractWaitTimeMinutes((String) b.get("waitTime"));
            })
            .collect(Collectors.toList());
    }
    
    private List<Map<String, String>> getRecentVerifications(List<VerificationCaseCheck> recentChecks) {
        return recentChecks.stream()
            .map(check -> {
                String status = getStatusMapping(check.getStatus());
                String timeAgo = getTimeAgo(check.getUpdatedAt());
                
                return Map.of(
                    "id", String.valueOf(check.getCaseCheckId()),
                    "checkType", check.getCategory() != null ? 
                        check.getCategory().getCode().toLowerCase() : "unknown",
                    "caseId", "CASE-" + check.getVerificationCase().getCaseId(),
                    "status", status,
                    "time", timeAgo
                );
            })
            .collect(Collectors.toList());
    }
    
 // Helper method remains the same
    private String determineCheckPriority(VerificationCaseCheck check) {
        String slaStatus = calculateSlaStatus(check);
        long daysRemaining = calculateDaysRemaining(check);
        
        if ("critical".equals(slaStatus) || daysRemaining <= 1) return "high";
        if ("warning".equals(slaStatus) || daysRemaining <= 3) return "medium";
        return "low";
    }
    private long calculateDaysRemaining(VerificationCaseCheck check) {
        // Calculate based on check's creation time
        LocalDateTime slaDeadline = check.getCreatedAt().plusDays(14);
        return ChronoUnit.DAYS.between(LocalDateTime.now(), slaDeadline);
    }
    
    // Helper methods
    private String getStatusMapping(CaseCheckStatus status) {
        if (status == null) return "pending";
        
        switch (status) {
            case COMPLETED: return "completed";
            case IN_PROGRESS: return "in_progress";
            case PENDING: return "pending";
            case ON_HOLD: return "on_hold";
          //  case DELAYED: return "delayed";
            case INSUFFICIENT: return "insufficient";
            case ASSIGNED: return "assigned";
            case AGENT_ASSIGNED: return "AGENT_ASSIGNED";
            default: return "pending";
        }
    }
    
    private String calculateSlaStatus(VerificationCaseCheck check) {
        // Implement SLA calculation logic
        if (check.getStatus() == CaseCheckStatus.ON_HOLD) {
            return "critical";
        } else if (check.getStatus() == CaseCheckStatus.AWAITING_CANDIDATE) {
            return "warning";
        } else {
            return "normal";
        }
    }
    
    private String calculateOverallSlaStatus(
            VerificationCase verificationCase,
            List<CheckSummaryDto> checks) {

        boolean hasCritical = checks.stream()
                .anyMatch(c -> "critical".equalsIgnoreCase(c.getSlaStatus()));

        boolean hasWarning = checks.stream()
                .anyMatch(c -> "warning".equalsIgnoreCase(c.getSlaStatus()));

        if (hasCritical) {
            return "critical";
        }

        if (hasWarning) {
            return "warning";
        }

        return "normal";
    }
    
    private long calculateDaysRemaining(VerificationCase verificationCase) {
        // Assuming SLA is 14 days from creation
        LocalDateTime slaDeadline = verificationCase.getCreatedAt().plusDays(14);
        return ChronoUnit.DAYS.between(LocalDateTime.now(), slaDeadline);
    }
    
    private String determinePriority(
            VerificationCase verificationCase,
            List<CheckSummaryDto> checks) {

        long criticalCount = checks.stream()
                .filter(c -> "critical".equalsIgnoreCase(c.getSlaStatus()))
                .count();

        long warningCount = checks.stream()
                .filter(c -> "warning".equalsIgnoreCase(c.getSlaStatus()))
                .count();

        if (criticalCount > 0) {
            return "high";
        }

        if (warningCount > 0
                || calculateDaysRemaining(verificationCase) <= 3) {
            return "medium";
        }

        return "low";
    }
    
    private String determineCheckPriority(VerificationCase verificationCase, VerificationCaseCheck check) {
        String slaStatus = calculateSlaStatus(check);
        long daysRemaining = calculateDaysRemaining(verificationCase);
        
        if ("critical".equals(slaStatus) || daysRemaining <= 1) return "high";
        if ("warning".equals(slaStatus) || daysRemaining <= 3) return "medium";
        return "low";
    }
    
    private String calculateWaitTime(VerificationCaseCheck check) {
        // Calculate wait time based on when check was assigned/created
        LocalDateTime checkTime = check.getCreatedAt() != null ? check.getCreatedAt() : LocalDateTime.now();
        long hours = ChronoUnit.HOURS.between(checkTime, LocalDateTime.now());
        return hours + "h";
    }
    
    private boolean isSlaAtRisk(VerificationCase verificationCase) {
        long daysRemaining = calculateDaysRemaining(verificationCase);
        return daysRemaining <= 3 && daysRemaining > 0;
    }
    
    private boolean isSlaBreached(VerificationCase verificationCase) {
        long daysRemaining = calculateDaysRemaining(verificationCase);
        return daysRemaining < 0;
    }
    
    private int getPriorityValue(String priority) {
        switch (priority) {
            case "high": return 3;
            case "medium": return 2;
            case "low": return 1;
            default: return 0;
        }
    }
    
    private int extractWaitTimeMinutes(String waitTime) {
        // Convert "2h" to minutes
        if (waitTime.endsWith("h")) {
            return Integer.parseInt(waitTime.replace("h", "")) * 60;
        }
        return 0;
    }
    
    private String getTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown time";
        
        long hours = ChronoUnit.HOURS.between(dateTime, LocalDateTime.now());
        
        if (hours < 1) {
            long minutes = ChronoUnit.MINUTES.between(dateTime, LocalDateTime.now());
            return minutes + " minutes ago";
        } else if (hours < 24) {
            return hours + " hours ago";
        } else {
            long days = ChronoUnit.DAYS.between(dateTime, LocalDateTime.now());
            return days + " days ago";
        }
    }
}