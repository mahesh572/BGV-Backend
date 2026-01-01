package com.org.bgv.controller;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.VerificationCaseResponse;
import com.org.bgv.common.VerificationCaseRequest;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.service.VerificationCaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/verification-cases")
@RequiredArgsConstructor
@Slf4j
public class VerificationCaseController {
    
    private final VerificationCaseService verificationCaseService;
    
    @PostMapping
    public ResponseEntity<CustomApiResponse<VerificationCaseResponse>> createVerificationCase(
            @Valid @RequestBody VerificationCaseRequest request) {
        try {
            VerificationCaseResponse response = verificationCaseService.createVerificationCase(request);
            return ResponseEntity.ok(CustomApiResponse.success("verification case created successfully", response, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to create verification case: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/case/{caseId}")
    public ResponseEntity<CustomApiResponse<VerificationCaseResponse>> getCandidateCase(@PathVariable Long caseId) {
        try {
            VerificationCaseResponse response = verificationCaseService.getVerificationCase(caseId);
            return ResponseEntity.ok(CustomApiResponse.success("verification case retrieved successfully", response, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve verification case: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<CustomApiResponse<List<VerificationCaseResponse>>> getCandidateCasesByCandidate(
            @PathVariable Long candidateId) {
        try {
            List<VerificationCaseResponse> responses = verificationCaseService.getVerificationCasesByCandidate(candidateId);
            return ResponseEntity.ok(CustomApiResponse.success("verification cases retrieved successfully", responses, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve verification cases: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/company/{companyId}")
    public ResponseEntity<CustomApiResponse<List<VerificationCaseResponse>>> getCandidateCasesByCompany(
            @PathVariable Long companyId) {
        try {
            List<VerificationCaseResponse> responses = verificationCaseService.getVerificationCasesByCompany(companyId);
            return ResponseEntity.ok(CustomApiResponse.success("verification cases retrieved successfully", responses, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve verification cases: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/company/{companyId}/status/{status}")
    public ResponseEntity<CustomApiResponse<List<VerificationCaseResponse>>> getCandidateCasesByCompanyAndStatus(
            @PathVariable Long companyId, @PathVariable CaseStatus status) {
        try {
            List<VerificationCaseResponse> responses = verificationCaseService.getVerificationCasesByCompanyAndStatus(companyId, status);
            return ResponseEntity.ok(CustomApiResponse.success("verification cases retrieved successfully", responses, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve verification cases: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @PutMapping("/{caseId}/status")
    public ResponseEntity<CustomApiResponse<VerificationCaseResponse>> updateCaseStatus(
            @PathVariable Long caseId, @RequestParam CaseStatus status) {
        try {
            VerificationCaseResponse response = verificationCaseService.updateCaseStatus(caseId, status);
            return ResponseEntity.ok(CustomApiResponse.success("verification case status updated successfully", response, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update verification case status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/employer-package/{employerPackageId}")
    public ResponseEntity<CustomApiResponse<List<VerificationCaseResponse>>> getCandidateCasesByEmployerPackage(
            @PathVariable Long employerPackageId) {
        try {
            List<VerificationCaseResponse> responses = verificationCaseService.getVerificationCasesByEmployerPackage(employerPackageId);
            return ResponseEntity.ok(CustomApiResponse.success("verification cases retrieved successfully", responses, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve verification cases: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/company/{companyId}/pending")
    public ResponseEntity<CustomApiResponse<List<VerificationCaseResponse>>> getPendingCandidateCases(
            @PathVariable Long companyId) {
        try {
            List<VerificationCaseResponse> responses = verificationCaseService.getPendingCandidateCases(companyId);
            return ResponseEntity.ok(CustomApiResponse.success("Pending verification cases retrieved successfully", responses, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve pending verification cases: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/company/{companyId}/completed")
    public ResponseEntity<CustomApiResponse<List<VerificationCaseResponse>>> getCompletedCandidateCases(
            @PathVariable Long companyId) {
        try {
            List<VerificationCaseResponse> responses = verificationCaseService.getCompletedCandidateCases(companyId);
            return ResponseEntity.ok(CustomApiResponse.success("Completed verification cases retrieved successfully", responses, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve completed verification cases: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/candidate/{candidateId}/case/{caseId}/documents/sections")
    public ResponseEntity<CustomApiResponse<List<String>>> 
    getCandidateVerificationSectionsDocuments(
    		@PathVariable Long candidateId,
    		@PathVariable Long caseId
    		) {
        try {
        	
        	log.info("getCandidateVerificationSectionsDocuments:::::::::::{}",candidateId);
            List<String> sectionsList =
                    verificationCaseService.getSectionsForDocumentVerificationCase(candidateId,caseId);
          //  sectionsList.add("Other");
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Verification document sections retrieved successfully",
                            sectionsList,
                            HttpStatus.OK
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            CustomApiResponse.failure(
                                    "Failed to retrieve verification document sections: " + e.getMessage(),
                                    HttpStatus.INTERNAL_SERVER_ERROR
                            )
                    );
        }
    }

    
    
    
}