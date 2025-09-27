package com.org.bgv.service;

import com.org.bgv.common.ProfileStatus;
import com.org.bgv.controller.ProfileController;
import com.org.bgv.dto.BasicdetailsDTO;
import com.org.bgv.dto.ProfileDTO;
import com.org.bgv.entity.Profile;
import com.org.bgv.repository.ProfileRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final WorkExperienceService workExperienceService;
    private final ProfileAddressService profileAddressService;
    private final EducationService educationHistoryService;
    private final DocumentService documentService;
    private final IdentityProofService identityProofService;
    
    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);
    
    public ProfileDTO createProfile(ProfileDTO profileDTO) {
        Profile profile = mapToEntity(profileDTO);
        profile.setStatus(ProfileStatus.CREATED.name());
        System.out.println("profile==========="+profile.toString());
        Profile savedProfile = profileRepository.save(profile);
        return mapToDTO(savedProfile);
    }
    
 

    public ProfileDTO getCompleteProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));
        
        BasicdetailsDTO basicdetailsDTO = BasicdetailsDTO.builder()
            	.firstName(profile.getFirst_name())
            	.lastName(profile.getLast_name())
            	.email(profile.getEmail_address())
            	.dateOfBirth(profile.getDate_of_birth())
            	.phone(profile.getPhoneNumber())
            	.profileId(profile.getProfileId())
            	.gender(profile.getGender())
            	.user_id(profile.getUserId())
            	.verificationStatus(profile.getVerificationStatus()==null ?"":profile.getVerificationStatus())
            	.status(profile.getStatus())
            	.build();
        

        return ProfileDTO.builder()
               .basicDetails(basicdetailsDTO)
               // .workExperiences(workExperienceService.getWorkExperiencesByProfile(profileId))
                .workExperiences(workExperienceService.getWorkExperiencesWithDocuments(profileId).getWorkExperiences())
                .addresses(profileAddressService.getAddressesByProfile(profileId)) 
                .educationHistory(educationHistoryService.getEducationByProfile(profileId))
                .Identity(identityProofService.getIdentityProofsWithDocuments(profileId).getProofs())
                .documents(documentService.getDocumentsByProfileGroupedByCategory(profileId))
                .build();
    }
    
    public ProfileDTO getProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));
        return mapToDTO(profile);
    }

    public ProfileDTO updateProfile(Long profileId, ProfileDTO profileDTO) {
        Profile existingProfile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));
        BasicdetailsDTO basicdetailsDTO  = profileDTO.getBasicDetails();
        existingProfile.setFirst_name(basicdetailsDTO.getFirstName());
        existingProfile.setLast_name(basicdetailsDTO.getLastName());
        existingProfile.setEmail_address(basicdetailsDTO.getEmail());
        existingProfile.setPhoneNumber(basicdetailsDTO.getPhone());
        existingProfile.setDate_of_birth(basicdetailsDTO.getDateOfBirth());
        existingProfile.setGender(basicdetailsDTO.getGender());

        Profile updatedProfile = profileRepository.save(existingProfile);
        return mapToDTO(updatedProfile);
    }

    @Transactional
    public void deleteProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));
        
        identityProofService.deleteIdentityByprofileId(profileId);
        // education history
        educationHistoryService.deleteAllEducationByProfile(profileId);
        // work history
        workExperienceService.deleteWorkexperience(profileId);
        // address
        profileAddressService.deleteProfileAddressByProfileId(profileId);
        
        documentService.deleteOtherDocuments(profileId);
        
        // documents
        
        
        profileRepository.delete(profile);
    }

    public List<ProfileDTO> getAllProfiles() {
        List<Profile> profiles = profileRepository.findAll();
        return profiles.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private Profile mapToEntity(ProfileDTO dto) {
    	
        return Profile.builder()
                .profileId(dto.getBasicDetails().getProfileId())
                .first_name(dto.getBasicDetails().getFirstName())
                .last_name(dto.getBasicDetails().getLastName())
                .email_address(dto.getBasicDetails().getEmail())
                .phoneNumber(dto.getBasicDetails().getPhone())
                .date_of_birth(dto.getBasicDetails().getDateOfBirth())
                .gender(dto.getBasicDetails().getGender())
               // .userId(dto.getUser_id())
                .userId(1l)
                .status("PENDING")
                .build();
    }

    private ProfileDTO mapToDTO(Profile entity) {
    	
    	BasicdetailsDTO basicdetailsDTO = BasicdetailsDTO.builder()
    	.firstName(entity.getFirst_name())
    	.lastName(entity.getLast_name())
    	.email(entity.getEmail_address())
    	.dateOfBirth(entity.getDate_of_birth())
    	.phone(entity.getPhoneNumber())
    	.profileId(entity.getProfileId())
    	.gender(entity.getGender())
    	.user_id(entity.getUserId())
    	.verificationStatus(entity.getVerificationStatus()==null ?"":entity.getVerificationStatus())
    	.status(entity.getStatus())
    	.build();
    	
        return ProfileDTO.builder()
                .basicDetails(basicdetailsDTO)                            
                .workExperiences(workExperienceService.getWorkExperiencesByProfile(entity.getProfileId()))
                .addresses(profileAddressService.getAddressesByProfile(entity.getProfileId())) 
                .educationHistory(educationHistoryService.getEducationByProfile(entity.getProfileId()))
                .documents(documentService.getDocumentsByProfileGroupedByCategory(entity.getProfileId()))
                .build();
    }
    
    @Transactional
    public String updateProfileStatus(Long profileId, String newStatus) {
        logger.info("Updating profile {} to status {}", profileId, newStatus);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with ID: " + profileId));

        profile.setStatus(newStatus); // Assuming Profile has a field: private ProfileStatus status;
        Profile updatedProfile = profileRepository.save(profile);

        logger.info("Profile {} status updated to {}", profileId, updatedProfile.getStatus());
        return "";
    }
}