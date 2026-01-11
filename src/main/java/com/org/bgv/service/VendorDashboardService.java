package com.org.bgv.service;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.entity.*;
import com.org.bgv.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorDashboardService {
    
    private final VerificationCaseRepository verificationCaseRepository;
    private final VerificationCaseCheckRepository verificationCaseCheckRepository;
    private final CheckCategoryRepository checkCategoryRepository;
    private final CompanyRepository companyRepository;
    private final CandidateRepository candidateRepository;
    
    
    private static final int DEFAULT_SLA_DAYS = 14;
    
   
    @Transactional(readOnly = true)
    public Map<String, Object> getVendorDashboardData(Long vendorId) {
        // Get all checks assigned to this vendor with specific statuses
       /*
    	List<VerificationCaseCheck> vendorChecks = verificationCaseCheckRepository
            .findByVendorIdAndStatusIn(
                vendorId,
                Arrays.asList(CaseStatus.ASSIGNED, CaseStatus.IN_PROGRESS, CaseStatus.PENDING)
            );
        */
    	List<VerificationCaseCheck> vendorChecks = verificationCaseCheckRepository
                .findByVendorIdOrderByUpdatedAtDesc(
                    vendorId
                );
        // Extract unique cases from checks
        List<VerificationCase> vendorCases = vendorChecks.stream()
            .map(VerificationCaseCheck::getVerificationCase)
            .distinct()
            .collect(Collectors.toList());
        
        return Map.of(
            "workload", getWorkloadData(vendorChecks),
            "verificationStats", getVerificationStats(vendorChecks),
            "activeCases", getActiveCases(vendorCases),
            "verificationQueue", getVerificationQueue(vendorChecks),
            "recentVerifications", getRecentVerifications(vendorChecks)
        );
    }
    
    private Map<String, Object> getWorkloadData(List<VerificationCaseCheck> checks) {
        long totalAssigned = checks.size();
        long inProgress = checks.stream()
            .filter(c -> c.getStatus() == CaseCheckStatus.PENDING)
            .count();
        long completed = checks.stream()
            .filter(c -> c.getStatus() == CaseCheckStatus.COMPLETED)
            .count();
        long pending = checks.stream()
            .filter(c -> c.getStatus() == CaseCheckStatus.PENDING)
            .count();
        
        return Map.of(
            "totalAssigned", totalAssigned,
            "inProgress", inProgress,
            "completed", completed,
            "pending", pending
        );
    }
    
    private Map<String, Map<String, Object>> getVerificationStats(List<VerificationCaseCheck> checks) {
        Map<String, Map<String, Object>> stats = new HashMap<>();
        
        // Group by check category
        Map<CheckCategory, List<VerificationCaseCheck>> checksByCategory = checks.stream()
            .filter(check -> check.getCategory() != null)
            .collect(Collectors.groupingBy(VerificationCaseCheck::getCategory));
        
        for (Map.Entry<CheckCategory, List<VerificationCaseCheck>> entry : checksByCategory.entrySet()) {
            CheckCategory category = entry.getKey();
            List<VerificationCaseCheck> categoryChecks = entry.getValue();
            
            String categoryCode = category.getCode().toLowerCase();
            
            Map<String, Object> categoryStats = Map.of(
                "assigned", categoryChecks.size(),
                "completed", categoryChecks.stream()
                    .filter(check -> check.getStatus() == CaseCheckStatus.COMPLETED)
                    .count(),
                "inProgress", categoryChecks.stream()
                    .filter(check -> check.getStatus() == CaseCheckStatus.PENDING)
                    .count(),
                "pending", categoryChecks.stream()
                    .filter(check -> check.getStatus() == CaseCheckStatus.PENDING)
                    .count()
            );
            
            stats.put(categoryCode, categoryStats);
        }
        
        return stats;
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
    
    private List<Map<String, Object>> getActiveCases(List<VerificationCase> cases) {
        List<Map<String, Object>> activeCases = new ArrayList<>();
        
        for (VerificationCase verificationCase : cases) {
            if (verificationCase.getStatus() == CaseStatus.COMPLETED) {
                continue;
            }
            
            // Get candidate info
            Candidate candidate = candidateRepository.findById(verificationCase.getCandidateId())
                .orElse(null);
            
            // Get company info
            Company company = companyRepository.findById(verificationCase.getCompanyId())
                .orElse(null);
            
            // Get case checks
            List<Map<String, String>> checks = verificationCase.getCaseChecks().stream()
                .map(check -> {
                    String checkType = check.getCategory() != null ? 
                        check.getCategory().getCode().toLowerCase() : "unknown";
                    String status = getStatusMapping(check.getStatus());
                    String sla = calculateSlaStatus(check);
                    
                    return Map.of(
                        "type", checkType,
                        "status", status,
                        "sla", sla,
                        "checkId",String.valueOf(check.getCaseCheckId()),
                        "check-ref",check.getCheckRef()!=null?check.getCheckRef():""
                    );
                })
                .collect(Collectors.toList());
            
            // Calculate overall SLA status
            String slaStatus = calculateOverallSlaStatus(verificationCase, checks);
            
            // Calculate days remaining
            long daysRemaining = calculateDaysRemaining(verificationCase);
            
            // Determine priority
            String priority = determinePriority(verificationCase, checks);
            
            Map<String, Object> caseData = Map.of(
                "id", verificationCase.getCaseId(),
                "candidate", candidate != null ? 
                    candidate.getProfile().getFirstName() + " " + candidate.getProfile().getLastName() : 
                    "Unknown Candidate",
                "employer", company != null ? company.getCompanyName() : "Unknown Company",
                "checks", checks,
                "slaStatus", slaStatus,
                "daysRemaining", daysRemaining,
                "priority", priority,
                "caseRef", verificationCase.getCaseNumber()!=null?verificationCase.getCaseNumber():""
            );
            
            activeCases.add(caseData);
        }
        
        return activeCases;
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
           // case INSUFFICIENT: return "insufficient";
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
    
    private String calculateOverallSlaStatus(VerificationCase verificationCase, List<Map<String, String>> checks) {
        boolean hasCritical = checks.stream().anyMatch(c -> "critical".equals(c.get("sla")));
        boolean hasWarning = checks.stream().anyMatch(c -> "warning".equals(c.get("sla")));
        
        if (hasCritical) return "critical";
        if (hasWarning) return "warning";
        return "normal";
    }
    
    private long calculateDaysRemaining(VerificationCase verificationCase) {
        // Assuming SLA is 14 days from creation
        LocalDateTime slaDeadline = verificationCase.getCreatedAt().plusDays(14);
        return ChronoUnit.DAYS.between(LocalDateTime.now(), slaDeadline);
    }
    
    private String determinePriority(VerificationCase verificationCase, List<Map<String, String>> checks) {
        long criticalCount = checks.stream().filter(c -> "critical".equals(c.get("sla"))).count();
        long warningCount = checks.stream().filter(c -> "warning".equals(c.get("sla"))).count();
        
        if (criticalCount > 0) return "high";
        if (warningCount > 0 || calculateDaysRemaining(verificationCase) <= 3) return "medium";
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