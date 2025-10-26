package com.org.bgv.controller;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.dto.*;
import com.org.bgv.dto.document.CategoriesDTO;
import com.org.bgv.dto.document.DocumentCategoryDto;
import com.org.bgv.service.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final WorkExperienceService workExperienceService;
    private final ProfileAddressService profileAddressService;
    private final EducationService educationHistoryService;
    private final DocumentService documentService;
    
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    // Create a new profile
    @PostMapping
    public ResponseEntity<CustomApiResponse<BasicdetailsDTO>> createProfile(@RequestBody BasicdetailsDTO profileDTO) {
        try {
        	
        	BasicdetailsDTO createdProfile = profileService.createProfile(profileDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success("Profile created successfully", createdProfile, HttpStatus.CREATED));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to create profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
 // Get all profiles
    @GetMapping
    public ResponseEntity<CustomApiResponse<List<ProfileDTO>>> getAllProfiles() {
        try {
            List<ProfileDTO> profiles = profileService.getAllProfiles();
            return ResponseEntity.ok(CustomApiResponse.success("Profiles retrieved successfully", profiles, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve profiles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Get complete profile with all details
    @GetMapping("/{profileId}/complete")
    public ResponseEntity<CustomApiResponse<ProfileDTO>> getCompleteProfile(@PathVariable Long profileId) {
        try {
            ProfileDTO profileDTO = profileService.getCompleteProfile(profileId);
            return ResponseEntity.ok(CustomApiResponse.success("Profile retrieved successfully", profileDTO, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Get basic profile info
    @GetMapping("/{profileId}")
    public ResponseEntity<CustomApiResponse<BasicdetailsDTO>> getProfile(@PathVariable Long profileId) {
        try {
        	BasicdetailsDTO profileDTO = profileService.getProfile(profileId);
            return ResponseEntity.ok(CustomApiResponse.success("Profile retrieved successfully", profileDTO, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Update profile
    @PutMapping("/{profileId}")
    public ResponseEntity<CustomApiResponse<BasicdetailsDTO>> updateProfile(@PathVariable Long profileId, @RequestBody BasicdetailsDTO profileDTO) {
        try {
        	logger.info("profileId:::UPDATE::::{}",profileId);
        	logger.info("ProfileDTO::::UPDATE:::{}",profileDTO);
        	BasicdetailsDTO updatedProfile = profileService.updateProfile(profileId, profileDTO);
            return ResponseEntity.ok(CustomApiResponse.success("Profile updated successfully", updatedProfile, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Delete profile
    @DeleteMapping("/{profileId}")
    public ResponseEntity<CustomApiResponse<Void>> deleteProfile(@PathVariable Long profileId) {
        try {
            profileService.deleteProfile(profileId);
            return ResponseEntity.ok(CustomApiResponse.success("Profile deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Education History endpoints
    @PostMapping("/{profileId}/education")
    public ResponseEntity<CustomApiResponse<List<EducationHistoryDTO>>> saveEducationHistory(
            @PathVariable Long profileId,
            @RequestBody List<EducationHistoryDTO> educationHistoryDTOs) {
        try {
            List<EducationHistoryDTO> savedEducation = educationHistoryService.saveEducationHistory(educationHistoryDTOs, profileId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success("Education history saved successfully", savedEducation, HttpStatus.CREATED));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to save education history: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @PutMapping("/{profileId}/education")
    public ResponseEntity<CustomApiResponse<List<EducationHistoryDTO>>> updateEducationHistory(
            @PathVariable Long profileId,
            @RequestBody List<EducationHistoryDTO> educationHistoryDTOs) {
        try {
        	logger.info("profile controller:::::::{}",profileId);
            List<EducationHistoryDTO> updatedEducation = educationHistoryService.updateEducationHistories(educationHistoryDTOs, profileId);
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Education history updated successfully", updatedEducation, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update education history: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @GetMapping("/{profileId}/education")
    public ResponseEntity<CustomApiResponse<List<EducationHistoryDTO>>> getEducationHistory(@PathVariable Long profileId) {
        try {
            List<EducationHistoryDTO> educationHistory = educationHistoryService.getEducationByProfile(profileId);
            return ResponseEntity.ok(CustomApiResponse.success("Education history retrieved successfully", educationHistory, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve education history: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @DeleteMapping("/{profileId}/education/{educationId}")
    public ResponseEntity<CustomApiResponse<Void>> deleteEducationHistory(@PathVariable Long profileId,
            @PathVariable Long educationId) {
        try {
            educationHistoryService.deleteEducationHistory(profileId,educationId);
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Education history deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete education history: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    // Work Experiences endpoints
    @PostMapping("/{profileId}/work-experiences")
    public ResponseEntity<CustomApiResponse<List<WorkExperienceDTO>>> saveWorkExperiences(
            @PathVariable Long profileId,
            @RequestBody List<WorkExperienceDTO> workExperienceDTOs) {
        try {
            List<WorkExperienceDTO> savedExperiences = workExperienceService.saveWorkExperiences(workExperienceDTOs, profileId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success("Work experiences saved successfully", savedExperiences, HttpStatus.CREATED));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to save work experiences: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{profileId}/work-experiences")
    public ResponseEntity<CustomApiResponse<List<WorkExperienceDTO>>> getWorkExperiences(@PathVariable Long profileId) {
        try {
            List<WorkExperienceDTO> experiences = workExperienceService.getWorkExperiencesByProfile(profileId);
            return ResponseEntity.ok(CustomApiResponse.success("Work experiences retrieved successfully", experiences, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve work experiences: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @PutMapping("/{profileId}/work-experiences")
    public ResponseEntity<CustomApiResponse<List<WorkExperienceDTO>>> updateWorkExperiences(
            @PathVariable Long profileId,
            @RequestBody List<WorkExperienceDTO> workExperienceDTOs) {
        try {
            List<WorkExperienceDTO> updatedExperiences = workExperienceService.updateWorkExperiences(workExperienceDTOs, profileId);
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Work experiences updated successfully", updatedExperiences, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update work experiences: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @DeleteMapping("/{profileId}/work-experiences/{id}")
    public ResponseEntity<CustomApiResponse<Void>> deleteWorkExperience(
            @PathVariable Long profileId,
            @PathVariable Long id) {
        try {
            workExperienceService.deleteWorkExperience(profileId, id);
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Work experience deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete work experience: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    // Addresses endpoints
    @PostMapping("/{profileId}/addresses")
    public ResponseEntity<CustomApiResponse<List<ProfileAddressDTO>>> saveProfileAddresses(
            @PathVariable Long profileId,
            @RequestBody List<ProfileAddressDTO> profileAddressDTOs) {
        try {
            List<ProfileAddressDTO> savedAddresses = profileAddressService.saveProfileAddresses(profileAddressDTOs, profileId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success("Addresses saved successfully", savedAddresses, HttpStatus.CREATED));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to save addresses: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{profileId}/addresses")
    public ResponseEntity<CustomApiResponse<List<ProfileAddressDTO>>> getProfileAddresses(@PathVariable Long profileId) {
        try {
            List<ProfileAddressDTO> addresses = profileAddressService.getAddressesByProfile(profileId);
            return ResponseEntity.ok(CustomApiResponse.success("Addresses retrieved successfully", addresses, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve addresses: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    
    @PutMapping("/{profileId}/addresses")
    public ResponseEntity<CustomApiResponse<List<ProfileAddressDTO>>> updateProfileAddresses(
            @PathVariable Long profileId,
            @RequestBody List<ProfileAddressDTO> profileAddressDTOs) {
        try {
            List<ProfileAddressDTO> updatedAddresses = profileAddressService.updateProfileAddresses(profileAddressDTOs, profileId);
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Addresses updated successfully", updatedAddresses, HttpStatus.OK));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
           // log.error("Error updating addresses for profileId: {}", profileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update addresses", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @DeleteMapping("/{profileId}/addresses/{addressId}")
    public ResponseEntity<CustomApiResponse<String>> deleteProfileAddress(
            @PathVariable Long profileId,
            @PathVariable Long addressId) {
        try {
            profileAddressService.deleteProfileAddress(addressId, profileId);
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Address deleted successfully", "Address with ID: " + addressId + " has been deleted", HttpStatus.OK));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            // log.error("Error deleting address with ID: {} for profileId: {}", addressId, profileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete address", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/{profileId}/documents")
    public ResponseEntity<CustomApiResponse<CategoriesDTO>> getDocuments(@PathVariable Long profileId) {
        try {
            CategoriesDTO documents = documentService.getDocuments(profileId);
            return ResponseEntity.ok(CustomApiResponse.success("Documents retrieved successfully", documents, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve documents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
  
 // Upload multiple documents endpoint
    @PostMapping(value = "/{profileId}/documents/upload-multiple", consumes = "multipart/form-data")
    public ResponseEntity<CustomApiResponse<DocumentCategoryDto>> uploadMultipleDocuments(
            @PathVariable Long profileId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("documentTypeId") Long documentTypeId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "objectId", required = false) Long objectId) {
    	
    	logger.info("Received upload request for profile: {}", profileId);
    	
    	logger.info("DocumentTypeId: {}, CategoryId: {}", documentTypeId, categoryId);
        try {
        	DocumentCategoryDto uploadedDocuments = documentService.createDocuments(
                files, profileId,categoryId,documentTypeId,objectId);
            logger.info("***************************uploaded successfully");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success("Documents uploaded successfully", uploadedDocuments, HttpStatus.CREATED));
                    
        } catch (RuntimeException e) {
        	logger.error("Upload error:", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
        	logger.error("Upload error:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to upload documents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    /*
    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<CustomApiResponse<DeleteResponse>> deleteDocument(
            @PathVariable Long documentId) {
        
        try {
            DeleteResponse deleteResponse = documentService.deleteDocument(documentId);
            return ResponseEntity.ok(CustomApiResponse.success("Document deleted successfully", deleteResponse, HttpStatus.OK));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
   */
    @DeleteMapping("/documents/{docId}")
    public ResponseEntity<CustomApiResponse<DeleteResponse>> deleteDocument(
    		
    		@PathVariable Long docId
            ) {
    	// logger.info("Received upload request for profile: {}", profileId);
    	
    	// logger.info("DocumentTypeId: {}, CategoryId: {}", docTypeId, category);
        try {
            DeleteResponse deleteResponse = documentService.deleteDocument(docId);
            return ResponseEntity.ok(CustomApiResponse.success("Document deleted successfully", deleteResponse, HttpStatus.OK));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/documents/types/{categoryId}")
    public ResponseEntity<CustomApiResponse<List<DocumentTypeResponse>>> getDocumentTypesByCategoryIgnoreCase(
            @PathVariable Long categoryId) {
        
        try {
            List<DocumentTypeResponse> response = documentService.getDocumentTypesByCategoryId(categoryId);
            return ResponseEntity.ok(CustomApiResponse.success("Document types retrieved successfully", response, HttpStatus.OK));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve document types: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @GetMapping("/documents/types/category/{categoryName}")
    public ResponseEntity<CustomApiResponse<List<DocumentTypeResponse>>> getDocumentTypesByCategoryIgnoreCase(
            @PathVariable String categoryName) {
        
        try {
            List<DocumentTypeResponse> response = 
                documentService.getDocumentTypesByCategoryNameIgnoreCase(categoryName);
            return ResponseEntity.ok(CustomApiResponse.success("Document types retrieved successfully", response, HttpStatus.OK));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve document types: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/degreetypes/all")
    public ResponseEntity<CustomApiResponse<List<DegreeTypeResponse>>> getAllDegreeTypes() {
        try {
            List<DegreeTypeResponse> response = educationHistoryService.getAllDegreeTypes();
            return ResponseEntity.ok(CustomApiResponse.success("Degree types retrieved successfully", response, HttpStatus.OK));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve degree types: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/fieldofstudy/all")
    public ResponseEntity<CustomApiResponse<List<FieldOfStudyResponse>>> getAllFieldsOfStudy() {
        try {
            List<FieldOfStudyResponse> response = educationHistoryService.getAllFieldsOfStudy();
            return ResponseEntity.ok(CustomApiResponse.success("Fields of study retrieved successfully", response, HttpStatus.OK));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve fields of study: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @PutMapping("/{profileId}/status")
    public ResponseEntity<CustomApiResponse<String>> updateProfileStatus(
            @PathVariable Long profileId,
            @RequestParam String status) {
        try {
            String updatedProfile = profileService.updateProfileStatus(profileId, status);
            return ResponseEntity.ok(
                    CustomApiResponse.success("Profile status updated successfully", updatedProfile, HttpStatus.OK)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update profile status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
 // Get documents for specific section
    @GetMapping("/{profileId}/section")
    public ResponseEntity<CustomApiResponse<DocumentCategoryDto>> getDocumentsBySection(
            @PathVariable Long profileId,
            @RequestParam String section) {
        try {
        	DocumentCategoryDto categories = documentService.getDocumentsBySection(profileId, section);
            return ResponseEntity.ok(
                    CustomApiResponse.success("Documents retrieved successfully for section: " + section, categories, HttpStatus.OK)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure("Section not found: " + section, HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve documents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    
    
    
    
}