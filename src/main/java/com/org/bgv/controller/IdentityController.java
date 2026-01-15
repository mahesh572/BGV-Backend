package com.org.bgv.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.dto.DocumentUploadRequest;
import com.org.bgv.dto.IdentitySectionRequest;
import com.org.bgv.service.CompanyService;
import com.org.bgv.service.IdentityProofService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/identity")
@RequiredArgsConstructor
@Slf4j
public class IdentityController {
	
	private final IdentityProofService identityProofService;

	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadDocuments(@ModelAttribute IdentitySectionRequest request) {
	    // Handle validation, persist details, file storage, etc.
	    return ResponseEntity.ok("Documents processed");
	}
	

	@GetMapping("/candidate/{candidateId}/case/{caseId}")
	public ResponseEntity<CustomApiResponse<IdentitySectionRequest>> getIdentitySection( @PathVariable Long candidateId,@PathVariable Long caseId) {
	    try {
	        IdentitySectionRequest identitySectionRequest = identityProofService.createIdentitySectionResponse(candidateId,caseId);
	        return ResponseEntity.ok(CustomApiResponse.success(
	            "Identity section retrieved successfully", 
	            identitySectionRequest, 
	            HttpStatus.OK
	        ));
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(CustomApiResponse.failure(
	                    "Failed to retrieve identity section: " + e.getMessage(), 
	                    HttpStatus.INTERNAL_SERVER_ERROR
	                ));
	    }
	}
	
	@PutMapping("/candidate/{candidateId}/case/{caseId}/check/{checkId}/fields")
	public ResponseEntity<CustomApiResponse<?>> updateIdentityFields(
            @PathVariable Long candidateId,
            @PathVariable Long caseId,
            @PathVariable Long checkId,
            @RequestBody List<DocumentUploadRequest> updateRequests) {
        try {
        	log.info("Identity Controller:::::::{}{}",updateRequests,candidateId);
            // Validate input
            if (updateRequests == null || updateRequests.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(CustomApiResponse.failure("No update data provided", HttpStatus.BAD_REQUEST));
            }

            identityProofService.updateIdentityFields(candidateId,caseId,checkId,updateRequests);

            return ResponseEntity.ok(CustomApiResponse.success(
                "Identity fields updated successfully", 
                "", 
                HttpStatus.OK
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Failed to update identity fields: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
	
	
	
	
	
}
