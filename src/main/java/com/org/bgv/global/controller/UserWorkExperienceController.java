package com.org.bgv.global.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.controller.WorkExperienceController;
import com.org.bgv.dto.WorkExperienceDTO;
import com.org.bgv.global.service.UserWorkExperienceService;
import com.org.bgv.service.WorkExperienceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user/{userId}/work-experience")
@RequiredArgsConstructor
@Slf4j
public class UserWorkExperienceController {
	
	private final UserWorkExperienceService userWorkExperienceService;
	
	
	

    @GetMapping
    public ResponseEntity<CustomApiResponse<List<WorkExperienceDTO>>> getWorkExperiences(@PathVariable Long userId) {
        try {
            List<WorkExperienceDTO> experiences = userWorkExperienceService.getUserWorkExperiences(userId);
            return ResponseEntity.ok(CustomApiResponse.success("Work experiences retrieved successfully", experiences, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve work experiences: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
	
	
	@PostMapping
    public ResponseEntity<CustomApiResponse<List<WorkExperienceDTO>>> saveWorkExperiences(
            @PathVariable Long userId,
            @RequestBody List<WorkExperienceDTO> workExperienceDTOs) {
        try {
            List<WorkExperienceDTO> savedExperiences = userWorkExperienceService.saveUserWorkExperiences(userId,workExperienceDTOs);
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
	
	
	  @PutMapping
	    public ResponseEntity<CustomApiResponse<List<WorkExperienceDTO>>> updateWorkExperiences(
	            @PathVariable Long userId,
	            @RequestBody List<WorkExperienceDTO> workExperienceDTOs) {
	        try {
	            List<WorkExperienceDTO> updatedExperiences = userWorkExperienceService.updateUserWorkExperiences(userId,workExperienceDTOs);
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
	    
	  @DeleteMapping("/{id}")
	    public ResponseEntity<CustomApiResponse<Void>> deleteWorkExperience(
	            @PathVariable Long userId,
	            @PathVariable Long id) {
	        try {
	        	userWorkExperienceService.deleteWorkExperience(userId,id);
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
}
