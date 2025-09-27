package com.org.bgv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {
	
	private BasicdetailsDTO basicDetails;
    
    // Work experiences
    private List<WorkExperienceDTO> workExperiences;
    
    // Addresses
    private List<ProfileAddressDTO> addresses;
    
    // Education history
    private List<EducationHistoryDTO> educationHistory;
    
    private List<IdentityProofDTO> Identity;
    
    // Documents grouped by category
    private List<DocumentCategoryGroup> documents;
}