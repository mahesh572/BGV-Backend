package com.org.bgv.service;

import com.org.bgv.common.ProfileStatus;
import com.org.bgv.common.Status;
import com.org.bgv.controller.ProfileController;
import com.org.bgv.dto.BasicDetailsDTO;
import com.org.bgv.dto.ProfileDTO;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.User;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
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
    private final UserRepository userRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);
    
    public BasicDetailsDTO createProfile(BasicDetailsDTO profileDTO) {
    	Profile savedProfile = null;
    	try {
    	profileDTO.setStatus(Status.PENDING);
        Profile profile = mapToEntity(profileDTO);
        profile.setStatus(ProfileStatus.CREATED.name());
        System.out.println("profile==========="+profile.toString());
        savedProfile = profileRepository.save(profile);
    	}catch (Exception e) {
			e.printStackTrace();
		}
        return mapToBasicdetailsDTO(savedProfile);
    }
    
 

    public ProfileDTO getCompleteProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));
        
        BasicDetailsDTO basicdetailsDTO = BasicDetailsDTO.builder()
            	.firstName(profile.getFirstName())
            	.lastName(profile.getLastName())
            	.email(profile.getEmailAddress())
            	.dateOfBirth(profile.getDateOfBirth())
            	.phone(profile.getPhoneNumber())
            	.profileId(profile.getProfileId())
            	.gender(profile.getGender())
            	.user_id(profile.getUser().getUserId())
            	.verificationStatus(profile.getVerificationStatus()==null ?"":profile.getVerificationStatus())
            	.status(profile.getStatus())
            	.build();
        

        return ProfileDTO.builder()
               .basicDetails(basicdetailsDTO)
               // .workExperiences(workExperienceService.getWorkExperiencesByProfile(profileId))
                .workExperiences(workExperienceService.getWorkExperiencesWithDocuments(profileId).getWorkExperiences())
                .addresses(profileAddressService.getAddressesByProfile(profileId)) 
             //   .educationHistory(educationHistoryService.getEducationByProfile(profileId))
                .Identity(identityProofService.getIdentityProofsWithDocuments(profileId).getProofs())
                .documents(documentService.getDocumentsByProfileGroupedByCategory(profileId))
                .build();
    }
    
    public BasicDetailsDTO getProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));
        return mapToBasicdetailsDTO(profile);
    }

    public BasicDetailsDTO updateProfile(Long profileId, BasicDetailsDTO basicdetailsDTO) {
        Profile existingProfile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));
      //  BasicdetailsDTO basicdetailsDTO  = profileDTO.getBasicDetails();
        existingProfile.setFirstName(basicdetailsDTO.getFirstName());
        existingProfile.setLastName(basicdetailsDTO.getLastName());
        existingProfile.setEmailAddress(basicdetailsDTO.getEmail());
        existingProfile.setPhoneNumber(basicdetailsDTO.getPhone());
        existingProfile.setDateOfBirth(basicdetailsDTO.getDateOfBirth());
        existingProfile.setGender(basicdetailsDTO.getGender());
        existingProfile.setLinkedinUrl(basicdetailsDTO.getLinkedIn());

        Profile updatedProfile = profileRepository.save(existingProfile);
        return mapToBasicdetailsDTO(updatedProfile);
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

    private Profile mapToEntity(BasicDetailsDTO dto) {
    	
    	
    	 User user = userRepository.findById(dto.getUser_id())
                 .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUser_id()));
    	
        return Profile.builder()
              //  .profileId(dto.getBasicDetails().getProfileId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .emailAddress(dto.getEmail())
                .phoneNumber(dto.getPhone())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
               // .userId(dto.getUser_id())
                .user(user)
                .status(dto.getStatus())
                .build();
    }
    
    
    private BasicDetailsDTO mapToBasicdetailsDTO(Profile entity) {
    	
    	BasicDetailsDTO basicdetailsDTO = BasicDetailsDTO.builder()
    	    	.firstName(entity.getFirstName())
    	    	.lastName(entity.getLastName())
    	    	.email(entity.getEmailAddress())
    	    	.dateOfBirth(entity.getDateOfBirth())
    	    	.phone(entity.getPhoneNumber())
    	    	.profileId(entity.getProfileId())
    	    	.gender(entity.getGender())
    	    	.user_id(entity.getUser().getUserId())
    	    	.verificationStatus(entity.getVerificationStatus()==null ?"":entity.getVerificationStatus())
    	    	.status(entity.getStatus())
    	    	.linkedIn(entity.getLinkedinUrl())
    	    	.build();
    	    	
    	return basicdetailsDTO;
    }
    

    private ProfileDTO mapToDTO(Profile entity) {
    	
    	BasicDetailsDTO basicdetailsDTO = BasicDetailsDTO.builder()
    	.firstName(entity.getFirstName())
    	.lastName(entity.getLastName())
    	.email(entity.getEmailAddress())
    	.dateOfBirth(entity.getDateOfBirth())
    	.phone(entity.getPhoneNumber())
    	.profileId(entity.getProfileId())
    	.gender(entity.getGender())
    	.user_id(entity.getUser().getUserId())
    	.verificationStatus(entity.getVerificationStatus()==null ?"":entity.getVerificationStatus())
    	.status(entity.getStatus())
    	.build();
    	
        return ProfileDTO.builder()
                .basicDetails(basicdetailsDTO)                            
                .workExperiences(workExperienceService.getWorkExperiencesByProfile(entity.getProfileId()))
                .addresses(profileAddressService.getAddressesByProfile(entity.getProfileId())) 
           //     .educationHistory(educationHistoryService.getEducationByProfile(entity.getProfileId()))
               // .documents(documentService.getDocumentsByProfileGroupedByCategory(entity.getProfileId()))
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
    
    public Long getProfileIdByUserId(Long userId) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }

            // Method 1: Using the query method to get only profile ID
            return profileRepository.findProfileIdByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found for user ID: " + userId));
                    
        } catch (Exception e) {
        	logger.error("Failed to get profile ID for user ID: {}", userId, e);
            throw new RuntimeException("Failed to get profile ID: " + e.getMessage());
        }
    }
    
    
    // verification 
    
    @Cacheable(value = "basicDetails", key = "#candidateId")
    public BasicDetailsDTO getBasicDetails(Long candidateId) {
    	logger.info("Fetching basic details for candidate: {}", candidateId);
        
        Profile profile = profileRepository.findByCandidateId(candidateId)
            .orElseThrow(() -> new EntityNotFoundException("Candidate profile not found"));
        
        return convertToDTO(profile);
    }
    
    private BasicDetailsDTO convertToDTO(Profile profile) {
        BasicDetailsDTO dto = new BasicDetailsDTO();
        dto.setCandidateId(profile.getCandidateId());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setEmail(profile.getEmailAddress());
        dto.setPhone(profile.getPhoneNumber());
        dto.setDateOfBirth(profile.getDateOfBirth());
        dto.setGender(profile.getGender());
        dto.setLinkedIn(profile.getLinkedinUrl());
        dto.setNationality(profile.getNationality());
        dto.setPassportNumber(profile.getPassportNumber());
        dto.setPassportExpiry(profile.getPassportExpiry());
        dto.setVerified(profile.getVerified());
        dto.setVerifiedAt(profile.getVerifiedAt());
        dto.setVerifiedBy(profile.getVerifiedBy());
        return dto;
    }

}