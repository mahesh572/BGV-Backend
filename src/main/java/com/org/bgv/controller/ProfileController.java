package com.org.bgv.controller;

import com.org.bgv.api.response.ApiResponse;
import com.org.bgv.dto.*;
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
    public ResponseEntity<ApiResponse<ProfileDTO>> createProfile(@RequestBody ProfileDTO profileDTO) {
        try {
        	
            ProfileDTO createdProfile = profileService.createProfile(profileDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Profile created successfully", createdProfile, HttpStatus.CREATED));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to create profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
 // Get all profiles
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfileDTO>>> getAllProfiles() {
        try {
            List<ProfileDTO> profiles = profileService.getAllProfiles();
            return ResponseEntity.ok(ApiResponse.success("Profiles retrieved successfully", profiles, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to retrieve profiles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Get complete profile with all details
    @GetMapping("/{profileId}/complete")
    public ResponseEntity<ApiResponse<ProfileDTO>> getCompleteProfile(@PathVariable Long profileId) {
        try {
            ProfileDTO profileDTO = profileService.getCompleteProfile(profileId);
            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profileDTO, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to retrieve profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Get basic profile info
    @GetMapping("/{profileId}")
    public ResponseEntity<ApiResponse<ProfileDTO>> getProfile(@PathVariable Long profileId) {
        try {
            ProfileDTO profileDTO = profileService.getProfile(profileId);
            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profileDTO, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to retrieve profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Update profile
    @PutMapping("/{profileId}")
    public ResponseEntity<ApiResponse<ProfileDTO>> updateProfile(@PathVariable Long profileId, @RequestBody ProfileDTO profileDTO) {
        try {
        	logger.info("profileId:::UPDATE::::{}",profileId);
        	logger.info("ProfileDTO::::UPDATE:::{}",profileDTO);
            ProfileDTO updatedProfile = profileService.updateProfile(profileId, profileDTO);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to update profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Delete profile
    @DeleteMapping("/{profileId}")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(@PathVariable Long profileId) {
        try {
            profileService.deleteProfile(profileId);
            return ResponseEntity.ok(ApiResponse.success("Profile deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to delete profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Education History endpoints
    @PostMapping("/{profileId}/education")
    public ResponseEntity<ApiResponse<List<EducationHistoryDTO>>> saveEducationHistory(
            @PathVariable Long profileId,
            @RequestBody List<EducationHistoryDTO> educationHistoryDTOs) {
        try {
            List<EducationHistoryDTO> savedEducation = educationHistoryService.saveEducationHistory(educationHistoryDTOs, profileId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Education history saved successfully", savedEducation, HttpStatus.CREATED));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to save education history: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @PutMapping("/{profileId}/education")
    public ResponseEntity<ApiResponse<List<EducationHistoryDTO>>> updateEducationHistory(
            @PathVariable Long profileId,
            @RequestBody List<EducationHistoryDTO> educationHistoryDTOs) {
        try {
            List<EducationHistoryDTO> updatedEducation = educationHistoryService.updateEducationHistories(educationHistoryDTOs, profileId);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Education history updated successfully", updatedEducation, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to update education history: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @GetMapping("/{profileId}/education")
    public ResponseEntity<ApiResponse<List<EducationHistoryDTO>>> getEducationHistory(@PathVariable Long profileId) {
        try {
            List<EducationHistoryDTO> educationHistory = educationHistoryService.getEducationByProfile(profileId);
            return ResponseEntity.ok(ApiResponse.success("Education history retrieved successfully", educationHistory, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to retrieve education history: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @DeleteMapping("/education/{educationId}")
    public ResponseEntity<ApiResponse<Void>> deleteEducationHistory(
            @PathVariable Long educationId) {
        try {
            educationHistoryService.deleteEducationHistory(educationId);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Education history deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to delete education history: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    // Work Experiences endpoints
    @PostMapping("/{profileId}/work-experiences")
    public ResponseEntity<ApiResponse<List<WorkExperienceDTO>>> saveWorkExperiences(
            @PathVariable Long profileId,
            @RequestBody List<WorkExperienceDTO> workExperienceDTOs) {
        try {
            List<WorkExperienceDTO> savedExperiences = workExperienceService.saveWorkExperiences(workExperienceDTOs, profileId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Work experiences saved successfully", savedExperiences, HttpStatus.CREATED));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to save work experiences: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{profileId}/work-experiences")
    public ResponseEntity<ApiResponse<List<WorkExperienceDTO>>> getWorkExperiences(@PathVariable Long profileId) {
        try {
            List<WorkExperienceDTO> experiences = workExperienceService.getWorkExperiencesByProfile(profileId);
            return ResponseEntity.ok(ApiResponse.success("Work experiences retrieved successfully", experiences, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to retrieve work experiences: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @PutMapping("/{profileId}/work-experiences")
    public ResponseEntity<ApiResponse<List<WorkExperienceDTO>>> updateWorkExperiences(
            @PathVariable Long profileId,
            @RequestBody List<WorkExperienceDTO> workExperienceDTOs) {
        try {
            List<WorkExperienceDTO> updatedExperiences = workExperienceService.updateWorkExperiences(workExperienceDTOs, profileId);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Work experiences updated successfully", updatedExperiences, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to update work experiences: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    // Addresses endpoints
    @PostMapping("/{profileId}/addresses")
    public ResponseEntity<ApiResponse<List<ProfileAddressDTO>>> saveProfileAddresses(
            @PathVariable Long profileId,
            @RequestBody List<ProfileAddressDTO> profileAddressDTOs) {
        try {
            List<ProfileAddressDTO> savedAddresses = profileAddressService.saveProfileAddresses(profileAddressDTOs, profileId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Addresses saved successfully", savedAddresses, HttpStatus.CREATED));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to save addresses: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{profileId}/addresses")
    public ResponseEntity<ApiResponse<List<ProfileAddressDTO>>> getProfileAddresses(@PathVariable Long profileId) {
        try {
            List<ProfileAddressDTO> addresses = profileAddressService.getAddressesByProfile(profileId);
            return ResponseEntity.ok(ApiResponse.success("Addresses retrieved successfully", addresses, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to retrieve addresses: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    
    @PutMapping("/{profileId}/addresses")
    public ResponseEntity<ApiResponse<List<ProfileAddressDTO>>> updateProfileAddresses(
            @PathVariable Long profileId,
            @RequestBody List<ProfileAddressDTO> profileAddressDTOs) {
        try {
            List<ProfileAddressDTO> updatedAddresses = profileAddressService.updateProfileAddresses(profileAddressDTOs, profileId);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Addresses updated successfully", updatedAddresses, HttpStatus.OK));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
           // log.error("Error updating addresses for profileId: {}", profileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to update addresses", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    
    // Documents endpoints
    @GetMapping("/{profileId}/documents")
    public ResponseEntity<ApiResponse<List<DocumentCategoryGroup>>> getDocuments(@PathVariable Long profileId) {
        try {
            List<DocumentCategoryGroup> documents = documentService.getDocumentsByProfileGroupedByCategory(profileId);
            return ResponseEntity.ok(ApiResponse.success("Documents retrieved successfully", documents, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to retrieve documents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    
    
 // Document Upload Configuration endpoints
    @GetMapping("/document-upload-config")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDocumentUploadConfig() {
        try {
            Map<String, Object> config = documentService.getDocumentUploadConfig();
            return ResponseEntity.ok(ApiResponse.success("Document upload configuration retrieved successfully", config, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to fetch document upload configuration: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

 // Upload multiple documents endpoint
    @PostMapping(value = "/{profileId}/documents/upload-multiple", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> uploadMultipleDocuments(
            @PathVariable Long profileId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("documentTypeId") Long documentTypeId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "objectId", required = false) Long objectId) {
    	
    	logger.info("Received upload request for profile: {}", profileId);
    	
    	logger.info("DocumentTypeId: {}, CategoryId: {}", documentTypeId, categoryId);
        try {
            List<DocumentResponse> uploadedDocuments = documentService.createDocuments(
                files, profileId,categoryId,documentTypeId,objectId);
            logger.info("***************************uploaded successfully");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Documents uploaded successfully", uploadedDocuments, HttpStatus.CREATED));
                    
        } catch (RuntimeException e) {
        	logger.error("Upload error:", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
        	logger.error("Upload error:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to upload documents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    /*
    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<ApiResponse<DeleteResponse>> deleteDocument(
            @PathVariable Long documentId) {
        
        try {
            DeleteResponse deleteResponse = documentService.deleteDocument(documentId);
            return ResponseEntity.ok(ApiResponse.success("Document deleted successfully", deleteResponse, HttpStatus.OK));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to delete document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
   */
    @DeleteMapping("/documents")
    public ResponseEntity<ApiResponse<DeleteResponse>> deleteDocument(
            @RequestParam Long docId,
            @RequestParam Long docTypeId, 
            @RequestParam String category) {

        try {
            DeleteResponse deleteResponse = documentService.deleteDocument(docId, docTypeId, category);
            return ResponseEntity.ok(ApiResponse.success("Document deleted successfully", deleteResponse, HttpStatus.OK));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to delete document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/documents/types/{categoryId}")
    public ResponseEntity<ApiResponse<List<DocumentTypeResponse>>> getDocumentTypesByCategoryIgnoreCase(
            @PathVariable Long categoryId) {
        
        try {
            List<DocumentTypeResponse> response = documentService.getDocumentTypesByCategoryId(categoryId);
            return ResponseEntity.ok(ApiResponse.success("Document types retrieved successfully", response, HttpStatus.OK));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to retrieve document types: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @GetMapping("/documents/types/category/{categoryName}")
    public ResponseEntity<ApiResponse<List<DocumentTypeResponse>>> getDocumentTypesByCategoryIgnoreCase(
            @PathVariable String categoryName) {
        
        try {
            List<DocumentTypeResponse> response = 
                documentService.getDocumentTypesByCategoryNameIgnoreCase(categoryName);
            return ResponseEntity.ok(ApiResponse.success("Document types retrieved successfully", response, HttpStatus.OK));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to retrieve document types: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/degreetypes/all")
    public ResponseEntity<ApiResponse<List<DegreeTypeResponse>>> getAllDegreeTypes() {
        try {
            List<DegreeTypeResponse> response = educationHistoryService.getAllDegreeTypes();
            return ResponseEntity.ok(ApiResponse.success("Degree types retrieved successfully", response, HttpStatus.OK));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to retrieve degree types: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/fieldofstudy/all")
    public ResponseEntity<ApiResponse<List<FieldOfStudyResponse>>> getAllFieldsOfStudy() {
        try {
            List<FieldOfStudyResponse> response = educationHistoryService.getAllFieldsOfStudy();
            return ResponseEntity.ok(ApiResponse.success("Fields of study retrieved successfully", response, HttpStatus.OK));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to retrieve fields of study: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @PutMapping("/{profileId}/status")
    public ResponseEntity<ApiResponse<String>> updateProfileStatus(
            @PathVariable Long profileId,
            @RequestParam String status) {
        try {
            String updatedProfile = profileService.updateProfileStatus(profileId, status);
            return ResponseEntity.ok(
                    ApiResponse.success("Profile status updated successfully", updatedProfile, HttpStatus.OK)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to update profile status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}