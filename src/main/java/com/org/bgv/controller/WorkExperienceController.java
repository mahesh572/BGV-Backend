package com.org.bgv.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.candidate.controller.EducationController;
import com.org.bgv.dto.WorkExperienceDTO;
import com.org.bgv.service.EducationService;
import com.org.bgv.service.WorkExperienceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/candidate/{candidateId}/work-experience")
@RequiredArgsConstructor
@Slf4j
public class WorkExperienceController {
	
	 private final WorkExperienceService workExperienceService;

	 // Work Experiences endpoints
    @PostMapping
    public ResponseEntity<CustomApiResponse<List<WorkExperienceDTO>>> saveWorkExperiences(
            @PathVariable Long candidateId,
            @RequestBody List<WorkExperienceDTO> workExperienceDTOs) {
        try {
            List<WorkExperienceDTO> savedExperiences = workExperienceService.saveWorkExperiences(workExperienceDTOs, candidateId);
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

    @GetMapping
    public ResponseEntity<CustomApiResponse<List<WorkExperienceDTO>>> getWorkExperiences(@PathVariable Long candidateId) {
        try {
            List<WorkExperienceDTO> experiences = workExperienceService.getWorkExperiencesByProfile(candidateId);
            return ResponseEntity.ok(CustomApiResponse.success("Work experiences retrieved successfully", experiences, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve work experiences: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @PutMapping
    public ResponseEntity<CustomApiResponse<List<WorkExperienceDTO>>> updateWorkExperiences(
            @PathVariable Long candidateId,
            @RequestBody List<WorkExperienceDTO> workExperienceDTOs) {
        try {
            List<WorkExperienceDTO> updatedExperiences = workExperienceService.updateWorkExperiences(workExperienceDTOs, candidateId);
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
            @PathVariable Long candidateId,
            @PathVariable Long id) {
        try {
            workExperienceService.deleteWorkExperience(candidateId, id);
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
