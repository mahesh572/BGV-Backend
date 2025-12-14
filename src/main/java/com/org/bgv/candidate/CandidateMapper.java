package com.org.bgv.candidate;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.org.bgv.common.CandidateDTO;
import com.org.bgv.entity.Candidate;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.User;

@Component
public class CandidateMapper {
    
    public CandidateDTO toDto(Candidate candidate) {
        if (candidate == null) {
            return null;
        }
        
        @lombok.NonNull
		User user = candidate.getUser();
        Company company = candidate.getCompany();
        
        
        return CandidateDTO.builder()
                .candidateId(candidate.getCandidateId())
                .sourceType(candidate.getSourceType())
                .userId(user != null ? user.getUserId() : null)
                .firstName(user != null ? user.getFirstName() : null)
                .lastName(user != null ? user.getLastName() : null)
                .email(user != null ? user.getEmail() : null)
                .mobileNo(user != null ? user.getPhoneNumber() : null)
                .isActive(candidate.getIsActive())
                .isVerified(candidate.getIsVerified())
                .verificationStatus(candidate.getVerificationStatus())
                .createdAt(candidate.getCreatedAt())
                .updatedAt(candidate.getUpdatedAt())
                .lastActiveAt(candidate.getLastActiveAt())
                .jobSearchStatus(candidate.getJobSearchStatus())
                .isConsentProvided(candidate.getIsConsentProvided())
                .companyId(company != null ? company.getId() : null)
                .companyName(company != null ? company.getCompanyName() : null)
                .name(getFullName(candidate))
                .gender(user.getGender())
                .build();
    }
    
    public Candidate toEntity(CandidateDTO candidateDto) {
        if (candidateDto == null) {
            return null;
        }
        
        return Candidate.builder()
                .candidateId(candidateDto.getCandidateId())
                .sourceType(candidateDto.getSourceType())
                .isActive(candidateDto.getIsActive())
                .isVerified(candidateDto.getIsVerified())
                .verificationStatus(candidateDto.getVerificationStatus())
                .jobSearchStatus(candidateDto.getJobSearchStatus())
                .isConsentProvided(candidateDto.getIsConsentProvided())
                // Note: User and Company should be set separately in service layer
                .build();
    }
    
    private String getFullName(Candidate candidate) {
        if (candidate.getUser() == null) {
            return "";
        }
        String firstName = candidate.getUser().getFirstName() != null ? candidate.getUser().getFirstName() : "";
        String lastName = candidate.getUser().getLastName() != null ? candidate.getUser().getLastName() : "";
        return (firstName + " " + lastName).trim();
    }
}