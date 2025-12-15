package com.org.bgv.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import com.org.bgv.entity.Company;
import com.org.bgv.entity.Profile;

@Component
public class CandidateDetailsMapper {
    
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
                .verificationChecks(getMockVerificationChecks()) // Mock data
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
    
    private List<VerificationCheckDTO> getMockVerificationChecks() {
        return List.of(
                VerificationCheckDTO.builder()
                        .id(1L)
                        .name("Employment Verification")
                        .description("Member / ex officio editor")
                        .status("verified")
                        .icon("💼")
                       // .category("EMPLOYMENT")
                        .build(),
                VerificationCheckDTO.builder()
                        .id(2L)
                        .name("Education Verification")
                        .description("Member - Degree Admission")
                        .status("verified")
                        .icon("🎓")
                       // .category("EDUCATION")
                        .build(),
                VerificationCheckDTO.builder()
                        .id(3L)
                        .name("Address Verification")
                        .description("Member / ex officio editor")
                        .status("in_progress")
                        .icon("🏠")
                       // .category("ADDRESS")
                        .build()
        );
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
