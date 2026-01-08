package com.org.bgv.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.vendor.dto.EvidenceResponseDTO;
import com.org.bgv.vendor.dto.VerificationEvidenceResponseDTO;
import com.org.bgv.vendor.service.VerificationEvidenceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cases/{caseId}/evidence")
@RequiredArgsConstructor
@Slf4j
public class VerificationEvidenceController {

    private final VerificationEvidenceService evidenceService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse<EvidenceResponseDTO>> uploadEvidence(
            @PathVariable Long caseId,
            @RequestParam("file") MultipartFile file,
            @RequestParam Long candidateId,
            @RequestParam Long caseCheckId,
            @RequestParam(required = false) Long objectId,
            @RequestParam(required = false) Long docTypeId,
            @RequestParam(required = false) String remarks,
            @RequestParam(defaultValue = "false") boolean confidential
    ) {

        try {
            EvidenceResponseDTO response = evidenceService.uploadEvidence(
                    caseId,
                    candidateId,
                    caseCheckId,
                    objectId,
                    docTypeId,
                    file,
                    remarks,
                    confidential
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success(
                            "Evidence uploaded successfully",
                            response,
                            HttpStatus.CREATED
                    ));

        } catch (IllegalArgumentException e) {
            log.warn("Evidence upload validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));

        } catch (Exception e) {
            log.error("Evidence upload failed", e);
            return ResponseEntity.internalServerError()
                    .body(CustomApiResponse.failure(
                            "Failed to upload evidence",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    @GetMapping
    public ResponseEntity<CustomApiResponse<VerificationEvidenceResponseDTO>> getEvidence(
            @PathVariable Long caseId,
            @RequestParam Long checkId
    ) {
        log.info("Fetching evidences | caseId={} | checkId={}", caseId, checkId);

        try {
        	
            VerificationEvidenceResponseDTO response =
            		evidenceService.buildEvidenceResponse(caseId, checkId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Evidence retrieved successfully",
                            response,
                            HttpStatus.OK
                    )
            );

        } catch (IllegalArgumentException e) {
            log.warn("Evidence retrieval validation failed: {}", e.getMessage());

            return ResponseEntity.badRequest()
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ));

        } catch (Exception e) {
            log.error("Evidence retrieval failed", e);

            return ResponseEntity.internalServerError()
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve evidence",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

}
