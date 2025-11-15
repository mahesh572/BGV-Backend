package com.org.bgv.controller;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.ConsentRequest;
import com.org.bgv.common.ConsentResponse;

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
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}