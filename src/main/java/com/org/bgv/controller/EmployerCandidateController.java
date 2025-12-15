package com.org.bgv.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.CandidateDetailsDTO;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.service.CandidateService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/company/candidate")
@RequiredArgsConstructor
@Slf4j
public class EmployerCandidateController {
	
	 private final CandidateService candidateService;
	 
	 @GetMapping("/{candidateId}")
	 public ResponseEntity<CustomApiResponse<CandidateDetailsDTO>> getCandidateDetails(
	         @PathVariable Long candidateId) {
	     
		 Long companyId = SecurityUtils.getCurrentUserCompanyId();
		 
	     try {
	         log.info("Request received for candidate details with candidateId: {}", candidateId);
	         
	         if (candidateId == null || candidateId <= 0) {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                     .body(CustomApiResponse.failure(
	                         "Invalid candidate ID. Candidate ID must be a positive number", 
	                         HttpStatus.BAD_REQUEST));
	         }
	         
	         CandidateDetailsDTO candidateDetails = candidateService.getCandidateDetails(companyId,candidateId);
	         
	         if (candidateDetails == null) {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                     .body(CustomApiResponse.failure(
	                         "Candidate not found with ID: " + candidateId, 
	                         HttpStatus.NOT_FOUND));
	         }
	         
	         return ResponseEntity.ok(
	             CustomApiResponse.success(
	                 "Candidate details fetched successfully", 
	                 candidateDetails, 
	                 HttpStatus.OK
	             )
	         );
	         
	     } catch (EntityNotFoundException e) {
	         log.error("Candidate not found with ID: {}", candidateId, e);
	         return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                 .body(CustomApiResponse.failure(
	                     "Candidate not found: " + e.getMessage(), 
	                     HttpStatus.NOT_FOUND));
	                     
	     } catch (IllegalArgumentException e) {
	         log.error("Invalid request for candidate ID: {}", candidateId, e);
	         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                 .body(CustomApiResponse.failure(
	                     "Invalid request: " + e.getMessage(), 
	                     HttpStatus.BAD_REQUEST));
	                     
	     } catch (Exception e) {
	         log.error("Unexpected error fetching candidate details for ID: {}", candidateId, e);
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                 .body(CustomApiResponse.failure(
	                     "Internal server error while fetching candidate details", 
	                     HttpStatus.INTERNAL_SERVER_ERROR));
	     }
	 }

}
