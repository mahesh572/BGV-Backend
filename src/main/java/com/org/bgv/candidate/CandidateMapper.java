package com.org.bgv.candidate;

import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.common.CandidateDTO;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.User;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.EmailTemplateRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.service.FileReaderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CandidateMapper {
	
	 private final ProfileRepository profileRepository;
    
    public CandidateDTO toDto(Candidate candidate) {
        if (candidate == null) {
            return null;
        }
        
        @lombok.NonNull
		User user = candidate.getUser();
        Company company = candidate.getCompany();
        
        Profile profile =profileRepository.findByUserUserId(user.getUserId());
        
        
        return CandidateDTO.builder()
                .candidateId(candidate.getCandidateId())
                .sourceType(candidate.getSourceType())
                .userId(user != null ? user.getUserId() : null)
                .firstName(user != null ? profile.getFirstName() : null)
                .lastName(user != null ? profile.getLastName() : null)
                .email(user != null ? user.getEmail() : null)
                .mobileNo(user != null ? profile.getPhoneNumber() : null)
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
                .name(getFullName(candidate,profile))
                .gender(profile.getGender())
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
    
    private String getFullName(Candidate candidate,Profile profile) {
        if (candidate.getUser() == null) {
            return "";
        }
        String firstName = profile.getFirstName() != null ? profile.getFirstName() : "";
        String lastName = profile.getLastName() != null ? profile.getLastName() : "";
        return (firstName + " " + lastName).trim();
    }
}