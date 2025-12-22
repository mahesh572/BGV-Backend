package com.org.bgv.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.org.bgv.common.ActivityTimelineDTO;
import com.org.bgv.common.CandidateDetailsDTO;
import com.org.bgv.common.VPackageDTO;
import com.org.bgv.common.VerificationCheckDTO;
import com.org.bgv.dto.document.CompanyDto;
import com.org.bgv.entity.ActivityTimeline;
import com.org.bgv.entity.Candidate;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.repository.VerificationCaseRepository;
import com.org.bgv.service.IconService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CandidateDetailsMapper {
	
	private final IconService iconService;
	
	private final VerificationCaseRepository verificationCaseRepository;
	
	private final VerificationCaseCheckRepository verificationCaseCheckRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    
    public CandidateDetailsDTO toDTO(Candidate candidate) {
        
    	Profile profile = candidate.getProfile();
        
        return CandidateDetailsDTO.builder()
                .id(candidate.getCandidateId())
                .candidateId(candidate.getCandidateId()) // Using UUID as candidateId
                .name(getFullName(profile))
                .email(profile != null ? profile.getEmailAddress() : null)
                .phone(profile != null ? profile.getPhoneNumber() : null)
                .dateOfBirth(formatDate(profile != null ? profile.getDateOfBirth() : null))
                .gender(profile != null ? profile.getGender() : null)
                .pan("ABC04-11-14") // Mock PAN - you can map from actual entity
                .initials(getInitials(profile))
               // .uuid(candidate.getUuid())
               // .isActive(candidate.getIsActive())
              //  .isVerified(candidate.getIsVerified())
              //  .verificationStatus(candidate.getVerificationStatus())
              //  .jobSearchStatus(candidate.getJobSearchStatus())
              //  .isConsentProvided(candidate.getIsConsentProvided())
              //  .createdAt(candidate.getCreatedAt())
              //  .lastActiveAt(candidate.getLastActiveAt())
                .firstName(profile != null ? profile.getFirstName() : null)
                .lastName(profile != null ? profile.getLastName() : null)
                .nationality(profile != null ? profile.getNationality() : null)
                .maritalStatus(profile != null ? profile.getMaritalStatus() : null)
                .hasWorkExperience(profile != null ? profile.getHasWorkExperience() : null)
                .linkedinUrl(profile != null ? profile.getLinkedinUrl() : null)
             //   .company(mapCompany(candidate.getCompany()))
                .vpackage(getMockPackage()) // Mock data - replace with actual
                .verificationChecks(getVerificationChecks(candidate)) 
                .activityTimeline(mapActivityTimeline(candidate.getActivityTimeline()))
                .build();
    }
    
    private String getFullName(Profile profile) {
        if (profile == null) return null;
        return (profile.getFirstName() != null ? profile.getFirstName() + " " : "") +
               (profile.getLastName() != null ? profile.getLastName() : "");
    }
    
    private String getInitials(Profile profile) {
        if (profile == null) return "";
        String firstName = profile.getFirstName();
        String lastName = profile.getLastName();
        
        if (firstName != null && !firstName.isEmpty() && 
            lastName != null && !lastName.isEmpty()) {
            return String.valueOf(firstName.charAt(0)) + lastName.charAt(0);
        } else if (firstName != null && !firstName.isEmpty()) {
            return String.valueOf(firstName.charAt(0));
        }
        return "";
    }
    
    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }
    
    private CompanyDto mapCompany(Company company) {
        if (company == null) return null;
        
        return CompanyDto.builder()
                .companyId(company.getId())
                .companyName(company.getCompanyName())
               // .industry(company.getIndustry())
               // .size(company.getSize())
               // .location(company.getLocation())
                .build();
    }
    
    private List<ActivityTimelineDTO> mapActivityTimeline(List<ActivityTimeline> activities) {
        if (activities == null || activities.isEmpty()) {
            return getMockActivityTimeline();
        }
        
        return activities.stream()
                .map(this::mapActivityTimelineItem)
                .collect(Collectors.toList());
    }
    
    private ActivityTimelineDTO mapActivityTimelineItem(ActivityTimeline activity) {
        return ActivityTimelineDTO.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .timestamp(activity.getTimestamp().format(TIMESTAMP_FORMATTER))
                .icon(getIconForActivity(activity.getType()))
                .status(activity.getStatus().getValue())
                .type(activity.getType())
             //   .createdAt(activity.getCreatedAt())
                .build();
    }
    
    private String getIconForActivity(String type) {
        if (type == null) return "📝";
        return switch (type.toUpperCase()) {
            case "PROFILE_CREATED" -> "👤";
            case "DOCUMENT_UPLOAD" -> "📄";
            case "VERIFICATION" -> "✅";
            case "APPLICATION" -> "📋";
            case "INTERVIEW" -> "🎯";
            default -> "📝";
        };
    }
    
    // Mock data methods (replace with actual data from your entities)
    private VPackageDTO getMockPackage() {
        return VPackageDTO.builder()
                .name("Standard Background Check")
                .id("standard-bg-check")
                .price("12,500")
                .timeline("5-7 Business Days")
                .status("In Progress")
                .assignedDate("15 Jan 2024")
                .build();
    }
    
    private List<VerificationCheckDTO> getVerificationChecks(Candidate candidate) {
        // Using IconService to get icons
    	List<VerificationCheckDTO> verificationChecks = new ArrayList();
    	List<VerificationCase> verificationCases = verificationCaseRepository.findByCandidateId(candidate.getCandidateId());
    	
    	if (verificationCases.isEmpty()) {
            log.warn("No verification cases found for candidate ID: {}", candidate.getCandidateId());
            return Collections.emptyList();
        }
    	
    	// Process each verification case
        for (VerificationCase verificationCase : verificationCases) {
            // Find verification checks for this case
            List<VerificationCaseCheck> caseChecks = verificationCaseCheckRepository
                    .findByVerificationCase_CaseId(verificationCase.getCaseId());
            
            // Convert each check to DTO
            for (VerificationCaseCheck caseCheck : caseChecks) {
                VerificationCheckDTO dto = mapToVerificationCheckDTO(caseCheck);
                verificationChecks.add(dto);
            }
        }
        
        log.info("getVerificationChecks:::::::::::::::::::{}",verificationChecks);
        
        return verificationChecks;
        
    	/*
        return List.of(
                VerificationCheckDTO.builder()
                        .id(1L)
                        .name("Employment Verification")
                        .description("Member / ex officio editor")
                        .status("verified")
                        .icon(iconService.getIconForVerification("Employment Verification"))
                      //  .category("EMPLOYMENT")
                        .build(),
                VerificationCheckDTO.builder()
                        .id(2L)
                        .name("Education Verification")
                        .description("Member - Degree Admission")
                        .status("verified")
                        .icon(iconService.getIconForVerification("Education Verification"))
                      //  .category("EDUCATION")
                        .build(),
                VerificationCheckDTO.builder()
                        .id(3L)
                        .name("Address Verification")
                        .description("Member / ex officio editor")
                        .status("in_progress")
                        .icon(iconService.getIconForVerification("Address Verification"))
                     //   .category("ADDRESS")
                        .build(),
                VerificationCheckDTO.builder()
                        .id(4L)
                        .name("Criminal/Court Check")
                        .description("Court records verification")
                        .status("verified")
                        .icon(iconService.getIconForVerification("Criminal/Court Check"))
                     //   .category("LEGAL")
                        .build(),
                VerificationCheckDTO.builder()
                        .id(5L)
                        .name("Identity Verification")
                        .description("Aadhaar and PAN verification")
                        .status("verified")
                        .icon(iconService.getIconForVerification("Identity Verification"))
                     //   .category("IDENTITY")
                        .build(),
                VerificationCheckDTO.builder()
                        .id(6L)
                        .name("Reference Check")
                        .description("Professional references verification")
                        .status("pending")
                        .icon(iconService.getIconForVerification("Reference Check"))
                      //  .category("REFERENCE")
                        .build()
        );
        */
        
    }
    
    private VerificationCheckDTO mapToVerificationCheckDTO(VerificationCaseCheck caseCheck) {
        CheckCategory category = caseCheck.getCategory();
        String categoryName = category != null ? category.getName() : "Unknown";
        String description = category.getDescription();
        
        return VerificationCheckDTO.builder()
                .id(caseCheck.getCaseCheckId())
                .name(categoryName +" Verification")
                .description(description)
                .status(mapStatus(caseCheck.getStatus().name()))
                .icon(iconService.getIconForVerification(categoryName))
            //    .category(category != null ? category.getCode() : null)
                .build();
    }
    
    private String mapStatus(String dbStatus) {
        if (dbStatus == null) return "pending";
        
        return switch (dbStatus.toUpperCase()) {
            case "VERIFIED", "COMPLETED" -> "verified";
            case "IN_PROGRESS", "UNDER_REVIEW" -> "in_progress";
            case "ASSIGNED", "CREATED" -> "pending";
            case "REJECTED", "CANCELLED" -> "rejected";
            default -> "pending";
        };
    }
    
    private List<ActivityTimelineDTO> getMockActivityTimeline() {
        return List.of(
                ActivityTimelineDTO.builder()
                        .id(1L)
                        .title("Candidate Created")
                        .description("Candidate profile was created in the system")
                        .timestamp("16 Jan 2024 07:05 AM")
                        .icon("👤")
                        .status("completed")
                        .type("SYSTEM")
                        .build(),
                ActivityTimelineDTO.builder()
                        .id(2L)
                        .title("Profile Updated")
                        .description("Candidate updated their profile information")
                        .timestamp("16 Jan 2024 02:25 PM")
                        .icon("📝")
                        .status("completed")
                        .type("USER")
                        .build()
        );
    }
}
