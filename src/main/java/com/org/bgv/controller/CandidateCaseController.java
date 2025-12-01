package com.org.bgv.controller;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.CandidateCaseRequest;
import com.org.bgv.common.CandidateCaseResponse;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.service.CandidateCaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidate-cases")
@RequiredArgsConstructor
public class CandidateCaseController {
    
    private final CandidateCaseService candidateCaseService;
    
    @PostMapping
    public ResponseEntity<CustomApiResponse<CandidateCaseResponse>> createCandidateCase(
            @Valid @RequestBody CandidateCaseRequest request) {
        try {
            CandidateCaseResponse response = candidateCaseService.createCandidateCase(request);
            return ResponseEntity.ok(CustomApiResponse.success("Candidate case created successfully", response, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to create candidate case: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/{caseId}")
    public ResponseEntity<CustomApiResponse<CandidateCaseResponse>> getCandidateCase(@PathVariable Long caseId) {
        try {
            CandidateCaseResponse response = candidateCaseService.getCandidateCase(caseId);
            return ResponseEntity.ok(CustomApiResponse.success("Candidate case retrieved successfully", response, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve candidate case: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<CustomApiResponse<List<CandidateCaseResponse>>> getCandidateCasesByCandidate(
            @PathVariable Long candidateId) {
        try {
            List<CandidateCaseResponse> responses = candidateCaseService.getCandidateCasesByCandidate(candidateId);
            return ResponseEntity.ok(CustomApiResponse.success("Candidate cases retrieved successfully", responses, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve candidate cases: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/company/{companyId}")
    public ResponseEntity<CustomApiResponse<List<CandidateCaseResponse>>> getCandidateCasesByCompany(
            @PathVariable Long companyId) {
        try {
            List<CandidateCaseResponse> responses = candidateCaseService.getCandidateCasesByCompany(companyId);
            return ResponseEntity.ok(CustomApiResponse.success("Candidate cases retrieved successfully", responses, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve candidate cases: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/company/{companyId}/status/{status}")
    public ResponseEntity<CustomApiResponse<List<CandidateCaseResponse>>> getCandidateCasesByCompanyAndStatus(
            @PathVariable Long companyId, @PathVariable CaseStatus status) {
        try {
            List<CandidateCaseResponse> responses = candidateCaseService.getCandidateCasesByCompanyAndStatus(companyId, status);
            return ResponseEntity.ok(CustomApiResponse.success("Candidate cases retrieved successfully", responses, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve candidate cases: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @PutMapping("/{caseId}/status")
    public ResponseEntity<CustomApiResponse<CandidateCaseResponse>> updateCaseStatus(
            @PathVariable Long caseId, @RequestParam CaseStatus status) {
        try {
            CandidateCaseResponse response = candidateCaseService.updateCaseStatus(caseId, status);
            return ResponseEntity.ok(CustomApiResponse.success("Candidate case status updated successfully", response, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update candidate case status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/employer-package/{employerPackageId}")
    public ResponseEntity<CustomApiResponse<List<CandidateCaseResponse>>> getCandidateCasesByEmployerPackage(
            @PathVariable Long employerPackageId) {
        try {
            List<CandidateCaseResponse> responses = candidateCaseService.getCandidateCasesByEmployerPackage(employerPackageId);
            return ResponseEntity.ok(CustomApiResponse.success("Candidate cases retrieved successfully", responses, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve candidate cases: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/company/{companyId}/pending")
    public ResponseEntity<CustomApiResponse<List<CandidateCaseResponse>>> getPendingCandidateCases(
            @PathVariable Long companyId) {
        try {
            List<CandidateCaseResponse> responses = candidateCaseService.getPendingCandidateCases(companyId);
            return ResponseEntity.ok(CustomApiResponse.success("Pending candidate cases retrieved successfully", responses, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve pending candidate cases: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/company/{companyId}/completed")
    public ResponseEntity<CustomApiResponse<List<CandidateCaseResponse>>> getCompletedCandidateCases(
            @PathVariable Long companyId) {
        try {
            List<CandidateCaseResponse> responses = candidateCaseService.getCompletedCandidateCases(companyId);
            return ResponseEntity.ok(CustomApiResponse.success("Completed candidate cases retrieved successfully", responses, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve completed candidate cases: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}