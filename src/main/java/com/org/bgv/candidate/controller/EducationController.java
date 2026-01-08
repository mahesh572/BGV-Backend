package com.org.bgv.candidate.controller;

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
import com.org.bgv.controller.IdentityController;
import com.org.bgv.dto.EducationHistoryDTO;
import com.org.bgv.service.EducationService;
import com.org.bgv.service.IdentityProofService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/candidate/{candidateId}/education")
@RequiredArgsConstructor
@Slf4j
public class EducationController {
	
	private final EducationService educationHistoryService;
	
	// Education History endpoints
  //  @PostMapping("/{profileId}/education")
	  @PostMapping
    public ResponseEntity<CustomApiResponse<List<EducationHistoryDTO>>> saveEducationHistory(
            @PathVariable Long candidateId,
            @RequestParam(required = false) Long caseId,
            @RequestBody List<EducationHistoryDTO> educationHistoryDTOs) {
        try {
            List<EducationHistoryDTO> savedEducation = educationHistoryService.saveEducationHistory(educationHistoryDTOs, candidateId,caseId);
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
    @PutMapping
    public ResponseEntity<CustomApiResponse<List<EducationHistoryDTO>>> updateEducationHistory(
            @PathVariable Long candidateId,
            @RequestParam(required = false) Long caseId,
            @RequestBody List<EducationHistoryDTO> educationHistoryDTOs) {
        try {
        	log.info("Education controller:::::::{}",candidateId);
            List<EducationHistoryDTO> updatedEducation = educationHistoryService.updateEducationHistories(educationHistoryDTOs, candidateId,caseId);
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
    
    @GetMapping
    public ResponseEntity<CustomApiResponse<List<EducationHistoryDTO>>> getEducationHistory(@PathVariable Long candidateId,@RequestParam(required = false) Long caseId) {
        try {
            List<EducationHistoryDTO> educationHistory = educationHistoryService.getEducationByProfile(candidateId,caseId);
            return ResponseEntity.ok(CustomApiResponse.success("Education history retrieved successfully", educationHistory, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve education history: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @DeleteMapping("/{educationId}")
    public ResponseEntity<CustomApiResponse<Void>> deleteEducationHistory(@PathVariable Long candidateId,
            @PathVariable Long educationId) {
        try {
            educationHistoryService.deleteEducationHistory(candidateId,educationId);
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

}
