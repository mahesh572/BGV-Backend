package com.org.bgv.global.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.controller.IdentityController;
import com.org.bgv.dto.DocumentUploadRequest;
import com.org.bgv.dto.IdentitySectionRequest;
import com.org.bgv.global.dto.UserIdentityUpdateRequest;
import com.org.bgv.global.service.UserIdentityProofService;
import com.org.bgv.service.IdentityProofService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user/{userId}/identity")
@RequiredArgsConstructor
@Slf4j
public class UserIdentityController {
	
	private final UserIdentityProofService userIdentityProofService;
	
	
	@GetMapping
	public ResponseEntity<CustomApiResponse<IdentitySectionRequest>> getIdentitySection( @PathVariable Long userId) {
	    try {
	        IdentitySectionRequest identitySectionRequest = userIdentityProofService.createIdentitySectionResponse(userId);
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
	

	@PutMapping
	public ResponseEntity<CustomApiResponse<?>> updateIdentityFields(
            @PathVariable Long userId,
            @RequestBody List<UserIdentityUpdateRequest> updateRequests) {
        try {
        	log.info("Identity Controller:::::::{}{}",updateRequests,userId);
            // Validate input
            if (updateRequests == null || updateRequests.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(CustomApiResponse.failure("No update data provided", HttpStatus.BAD_REQUEST));
            }

            userIdentityProofService.updateIdentityFields(userId,updateRequests);

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
