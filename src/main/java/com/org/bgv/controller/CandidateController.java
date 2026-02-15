package com.org.bgv.controller;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.candidate.CandidateSearchRequest;
import com.org.bgv.common.CandidateDTO;
import com.org.bgv.common.ConsentRequest;
import com.org.bgv.common.ConsentResponse;
import com.org.bgv.common.FilterRequest;
import com.org.bgv.common.PaginationRequest;
import com.org.bgv.common.PaginationResponse;
import com.org.bgv.common.SortingRequest;
import com.org.bgv.service.CandidateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
@Slf4j
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping(value = "/consent", consumes = {"multipart/form-data"})
    public ResponseEntity<CustomApiResponse<ConsentResponse>> submitConsent(
            @Valid @ModelAttribute ConsentRequest consentRequest,
            HttpServletRequest request) {
        
        try {
            // Set audit information from request
            consentRequest.setIpAddress(getClientIpAddress(request));
            consentRequest.setUserAgent(request.getHeader("User-Agent"));

            log.info("Processing consent for candidate: {}, type: {}", 
                    consentRequest.getCandidateId(), consentRequest.getConsentType());

            ConsentResponse response = candidateService.saveConsent(consentRequest);
            return ResponseEntity.ok(CustomApiResponse.success("Consent saved successfully", response, HttpStatus.OK));

        } catch (RuntimeException e) {
            log.error("Error processing consent for candidate: {}", consentRequest.getCandidateId(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure("Failed to save consent: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            log.error("Unexpected error processing consent for candidate: {}", consentRequest.getCandidateId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Internal server error while saving consent", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{candidateId}/consents")
    public ResponseEntity<CustomApiResponse<List<ConsentResponse>>> getCandidateConsents(
            @PathVariable Long candidateId) {
        try {
            List<ConsentResponse> consents = candidateService.getConsentsByCandidateId(candidateId);
            if (consents.isEmpty()) {
                return ResponseEntity.ok(CustomApiResponse.success("No consents found for candidate", consents, HttpStatus.OK));
            }
            return ResponseEntity.ok(CustomApiResponse.success("Consents retrieved successfully", consents, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure("Candidate not found: " + e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("Error fetching consents for candidate: {}", candidateId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch consents", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{candidateId}/consents/type/{consentType}")
    public ResponseEntity<CustomApiResponse<List<ConsentResponse>>> getCandidateConsentsByType(
            @PathVariable Long candidateId,
            @PathVariable String consentType) {
        try {
            List<ConsentResponse> consents = candidateService.getConsentsByCandidateIdAndType(candidateId, consentType);
            if (consents.isEmpty()) {
                return ResponseEntity.ok(CustomApiResponse.success("No consents found for the specified type", consents, HttpStatus.OK));
            }
            return ResponseEntity.ok(CustomApiResponse.success("Consents retrieved successfully", consents, HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure("Invalid consent type: " + consentType, HttpStatus.BAD_REQUEST));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure("Candidate not found: " + e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("Error fetching consents for candidate: {} and type: {}", candidateId, consentType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch consents", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/consent/{consentId}")
    public ResponseEntity<CustomApiResponse<Map<String, String>>> deleteConsent(@PathVariable Long consentId) {
        try {
            candidateService.deleteConsent(consentId);
            return ResponseEntity.ok(CustomApiResponse.success(
                "Consent deleted successfully", 
                Map.of("message", "Consent deleted successfully"), 
                HttpStatus.OK
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure("Consent not found: " + e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("Error deleting consent: {}", consentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete consent", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/upload-signature")
    public ResponseEntity<CustomApiResponse<Map<String, String>>> uploadSignatureImage(
            @RequestParam("signatureImage") MultipartFile signatureImage) {
        try {
            String imageUrl = candidateService.uploadSignatureImage(signatureImage);
            return ResponseEntity.ok(CustomApiResponse.success(
                "Signature image uploaded successfully",
                Map.of("signatureUrl", imageUrl),
                HttpStatus.OK
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure("Failed to upload signature: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            log.error("Error uploading signature image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to upload signature image", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/consent/{consentId}")
    public ResponseEntity<CustomApiResponse<ConsentResponse>> getConsentById(@PathVariable Long consentId) {
        try {
            ConsentResponse consent = candidateService.getConsentById(consentId);
            return ResponseEntity.ok(CustomApiResponse.success("Consent found", consent, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure("Consent not found: " + e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("Error fetching consent: {}", consentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch consent", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    
    @GetMapping("/{candidateId}/has-consent")
    public ResponseEntity<CustomApiResponse<Boolean>> hasCandidateProvidedConsent(
            @PathVariable Long candidateId) {
        try {
            boolean hasConsent = candidateService.hasCandidateProvidedConsent(candidateId);
            String message = hasConsent ? 
                "Candidate has provided consent" : 
                "Candidate has not provided consent or not found";
            
            return ResponseEntity.ok(CustomApiResponse.success(
                message, 
                hasConsent, 
                HttpStatus.OK
            ));
            
        } catch (Exception e) {
            log.error("Error checking consent for candidate: {}", candidateId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Failed to check consent status", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    } 
    
    
    
    

    @GetMapping("/all")
    public ResponseEntity<CustomApiResponse<List<CandidateDTO>>> getAllCandidates() {
        try {
            log.info("Fetching all candidates");
            List<CandidateDTO> candidates = candidateService.getAllCandidates();
            return ResponseEntity.ok(CustomApiResponse.success("All candidates retrieved successfully", candidates, HttpStatus.OK));
        } catch (Exception e) {
            log.error("Error fetching all candidates", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch candidates", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/company/{companyId}/candidate/{candidateId}")
    public ResponseEntity<CustomApiResponse<CandidateDTO>> getCandidateById(@PathVariable Long companyId,@PathVariable Long candidateId) {
        try {
            log.info("Fetching candidate by ID: {}", candidateId);
            CandidateDTO candidate = candidateService.getCandidateById(companyId,candidateId);
            return ResponseEntity.ok(CustomApiResponse.success("Candidate found", candidate, HttpStatus.OK));
        } catch (RuntimeException e) {
            log.warn("Candidate not found with ID: {}", candidateId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure("Candidate not found: " + e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("Error fetching candidate with ID: {}", candidateId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch candidate", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<CustomApiResponse<CandidateDTO>> getCandidateByUserId(@PathVariable Long userId) {
        try {
            log.info("Fetching candidate by user ID: {}", userId);
            CandidateDTO candidate = candidateService.getCandidateByUserId(userId);
            if (candidate == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(CustomApiResponse.failure("Candidate not found for user ID: " + userId, HttpStatus.NOT_FOUND));
            }
            return ResponseEntity.ok(CustomApiResponse.success("Candidate found", candidate, HttpStatus.OK));
        } catch (Exception e) {
            log.error("Error fetching candidate for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch candidate for user", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
/*
    // FIXED: This is the original @PostMapping for creating candidates
    @PostMapping
    public ResponseEntity<CustomApiResponse<CandidateDTO>> createCandidate(@Valid @RequestBody CandidateDTO candidateDto) {
        try {
            log.info("Creating new candidate: {}", candidateDto.getEmail());
            // Fixed: Changed from addCandidate to createCandidate
            CandidateDTO createdCandidate = candidateService.createCandidate(candidateDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success("Candidate created successfully", createdCandidate, HttpStatus.CREATED));
        } catch (RuntimeException e) {
            log.error("Error creating candidate: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure("Failed to create candidate: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            log.error("Unexpected error creating candidate", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to create candidate", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
*/
    @PutMapping("/{candidateId}")
    public ResponseEntity<CustomApiResponse<CandidateDTO>> updateCandidate(
            @PathVariable Long candidateId,
            @Valid @RequestBody CandidateDTO candidateDto) {
        try {
            log.info("Updating candidate with ID: {}", candidateId);
            CandidateDTO updatedCandidate = candidateService.updateCandidate(candidateId, candidateDto);
            return ResponseEntity.ok(CustomApiResponse.success("Candidate updated successfully", updatedCandidate, HttpStatus.OK));
        } catch (RuntimeException e) {
            log.error("Error updating candidate with ID {}: {}", candidateId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure("Failed to update candidate: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            log.error("Unexpected error updating candidate with ID: {}", candidateId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update candidate", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    
    @DeleteMapping("/{candidateId}")
    public ResponseEntity<CustomApiResponse<Void>> deleteCandidate(@PathVariable Long candidateId) {
        try {
            log.info("Deleting candidate with ID: {}", candidateId);
            candidateService.deleteCandidate(candidateId);
            return ResponseEntity.ok(CustomApiResponse.success("Candidate deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            log.error("Error deleting candidate with ID {}: {}", candidateId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure("Failed to delete candidate: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            log.error("Unexpected error deleting candidate with ID: {}", candidateId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete candidate", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    
    @PatchMapping("/{candidateId}/verification")
    public ResponseEntity<CustomApiResponse<Void>> updateVerificationStatus(
            @PathVariable Long candidateId,
            @RequestParam String verificationStatus) {
        try {
            log.info("Updating verification status for candidate ID: {} to {}", candidateId, verificationStatus);
            candidateService.updateVerificationStatus(candidateId, verificationStatus);
            return ResponseEntity.ok(CustomApiResponse.success(
                    "Verification status updated successfully to " + verificationStatus, 
                    null, 
                    HttpStatus.OK));
        } catch (RuntimeException e) {
            log.error("Error updating verification status for candidate ID {}: {}", candidateId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure("Failed to update verification status: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            log.error("Unexpected error updating verification status for candidate ID: {}", candidateId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update verification status", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Additional endpoints with proper error handling

    @GetMapping("/company/{companyId}")
    public ResponseEntity<CustomApiResponse<List<CandidateDTO>>> getCandidatesByCompany(@PathVariable Long companyId) {
        try {
            log.info("Fetching candidates for company ID: {}", companyId);
            CandidateSearchRequest searchRequest = CandidateSearchRequest.builder()
                    .companyId(companyId)
                    .pagination(new PaginationRequest(0, 1000)) // Get all for company
                    .build();
            PaginationResponse<CandidateDTO> result = candidateService.searchCandidates(searchRequest);
            return ResponseEntity.ok(CustomApiResponse.success(
                    "Company candidates retrieved successfully", 
                    result.getContent(), 
                    HttpStatus.OK));
        } catch (Exception e) {
            log.error("Error fetching candidates for company ID: {}", companyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch company candidates", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<CustomApiResponse<List<CandidateDTO>>> getCandidatesByVerificationStatus(
            @PathVariable String status) {
        try {
            log.info("Fetching candidates with verification status: {}", status);
            CandidateSearchRequest searchRequest = CandidateSearchRequest.builder()
                    .pagination(new PaginationRequest(0, 1000))
                    .filters(List.of(new FilterRequest("verificationStatus", status, true)))
                    .build();
            PaginationResponse<CandidateDTO> result = candidateService.searchCandidates(searchRequest);
            return ResponseEntity.ok(CustomApiResponse.success(
                    "Candidates with status " + status + " retrieved successfully", 
                    result.getContent(), 
                    HttpStatus.OK));
        } catch (Exception e) {
            log.error("Error fetching candidates with status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch candidates by status", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}