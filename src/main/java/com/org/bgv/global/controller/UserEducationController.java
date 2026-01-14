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
import com.org.bgv.candidate.controller.EducationController;
import com.org.bgv.dto.EducationHistoryDTO;
import com.org.bgv.global.service.UserEducationService;
import com.org.bgv.service.EducationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user/{userId}/education")
@RequiredArgsConstructor
@Slf4j
public class UserEducationController {
	
	private final UserEducationService usereducationHistoryService;
	
	 @GetMapping
	    public ResponseEntity<CustomApiResponse<List<EducationHistoryDTO>>> getEducationHistory(@PathVariable Long userId) {
	        try {
	            List<EducationHistoryDTO> educationHistory = usereducationHistoryService.getUserEducationHistory(userId);
	            return ResponseEntity.ok(CustomApiResponse.success("Education history retrieved successfully", educationHistory, HttpStatus.OK));
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(CustomApiResponse.failure("Failed to retrieve education history: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
	        }
	    }
	
	@PostMapping
    public ResponseEntity<CustomApiResponse<List<EducationHistoryDTO>>> saveEducationHistory(
            @PathVariable Long userId,
            @RequestBody List<EducationHistoryDTO> educationHistoryDTOs) {
        try {
            List<EducationHistoryDTO> savedEducation = usereducationHistoryService.saveUserEducationHistory(educationHistoryDTOs, userId);
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
            @PathVariable Long userId,
            @RequestBody List<EducationHistoryDTO> educationHistoryDTOs) {
        try {
        	log.info("Education controller:::userId::::{}",userId);
        	 List<EducationHistoryDTO> updatedEducation = usereducationHistoryService.updateUserEducationHistories(userId,educationHistoryDTOs);
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
	
	@DeleteMapping("/{educationId}")
    public ResponseEntity<CustomApiResponse<Void>> deleteEducationHistory(@PathVariable Long userId,
            @PathVariable Long educationId) {
        try {
        	usereducationHistoryService.deleteEducationHistory(userId,educationId);
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
