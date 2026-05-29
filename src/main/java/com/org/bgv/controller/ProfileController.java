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
@RequestMapping("/api/user/{userId}/profiles")
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
    public ResponseEntity<CustomApiResponse<ProfileDTO>> createProfile(
            @RequestBody ProfileDTO profileDTO) {
        try {

            ProfileDTO createdProfile = profileService.createProfile(profileDTO);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success(
                            "Profile created successfully",
                            createdProfile,
                            HttpStatus.CREATED));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to create profile: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /*
 // Get all profiles
    @GetMapping("/all")
    public ResponseEntity<CustomApiResponse<List<ProfileDTO>>> getAllProfiles() {
        try {
            List<ProfileDTO> profiles = profileService.getAllProfiles();
            return ResponseEntity.ok(CustomApiResponse.success("Profiles retrieved successfully", profiles, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve profiles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
*/
    // Get complete profile with all details
    /*
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
*/
    
    
    // Get basic profile info
    @GetMapping("/{profileId}")
    public ResponseEntity<CustomApiResponse<ProfileDTO>> getProfile(
            @PathVariable Long profileId) {

        ProfileDTO profileDTO = profileService.getProfileById(profileId);

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Profile retrieved successfully",
                        profileDTO,
                        HttpStatus.OK));
    }
    
    @GetMapping
    public ResponseEntity<CustomApiResponse<ProfileDTO>> getProfileByUserId(
            @PathVariable Long userId) {

        ProfileDTO profileDTO = profileService.getProfileByUserId(userId);

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Profile retrieved successfully",
                        profileDTO,
                        HttpStatus.OK
                )
        );
    }


    // Update profile
    @PutMapping("/{profileId}")
    public ResponseEntity<CustomApiResponse<ProfileDTO>> updateProfile(
            @PathVariable Long profileId,
            @RequestBody ProfileDTO profileDTO) {

        logger.info("Updating profile with id {}", profileId);
        
        logger.info("Updating profile with profileDTO {}", profileDTO);

        ProfileDTO updatedProfile = profileService.updateProfile(profileId, profileDTO);

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Profile updated successfully",
                        updatedProfile,
                        HttpStatus.OK));
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
    
    
    @PutMapping("/{profileId}/status")
    public ResponseEntity<CustomApiResponse<String>> updateProfileStatus(
            @PathVariable Long profileId,
            @RequestParam String status) {
        try {
           // String updatedProfile = profileService.updateProfileStatus(profileId, status);
            return ResponseEntity.ok(
                    CustomApiResponse.success("Profile status updated successfully", "", HttpStatus.OK)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update profile status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    /*
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
    
    */
    
    @PostMapping("/{profileId}/upload-photo")
    public ResponseEntity<CustomApiResponse<String>> uploadProfilePhoto(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {

        try {
            profileService.uploadProfilePicture(userId, file);
            return ResponseEntity.ok(
                    CustomApiResponse.success("Profile picture uploaded successfully", null, HttpStatus.OK));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to upload profile picture: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    
}