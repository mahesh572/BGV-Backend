package com.org.bgv.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.candidate.dto.CandidateVerificationDTO;
import com.org.bgv.candidate.dto.CaseStatisticsDTO;
import com.org.bgv.candidate.dto.SectionStatusUpdateRequest;
import com.org.bgv.candidate.dto.VerificationCaseDTO;
import com.org.bgv.candidate.dto.VerificationCaseFilterDTO;
import com.org.bgv.candidate.dto.VerificationCaseResponseDTO;
import com.org.bgv.candidate.dto.VerificationSectionDTO;
import com.org.bgv.candidate.service.VerificationService;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.constants.SectionStatus;
import com.org.bgv.service.VerificationCaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/candidate/{candidateId}/verification")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Candidate Verification", description = "APIs for candidate verification management")
public class CandidateVerificationController {
    
    private final VerificationService verificationService;
    private final VerificationCaseService verificationCaseService;
    
    @Operation(summary = "Get candidate verification details")
    @GetMapping("/case/{caseId}")
    public ResponseEntity<CandidateVerificationDTO> getCandidateVerification(
            @PathVariable Long candidateId,
            @PathVariable Long caseId
            ) {
        
        log.info("GET /api/verification/candidate?candidateId={} requested by {}{}", candidateId,caseId);
        
        CandidateVerificationDTO verification = verificationService.getCandidateVerification(candidateId,caseId);
        return ResponseEntity.ok(verification);
    }
    
    @Operation(summary = "Update section status")
    @PutMapping("/section/status")
    public ResponseEntity<CandidateVerificationDTO> updateSectionStatus(
            @RequestBody SectionStatusUpdateRequest sectionStatusUpdateRequest
            ) throws ValidationException {
        
        log.info("PUT /api/verification/section/status?candidateId={}&section={}&status={} by {}", 
        		sectionStatusUpdateRequest.getCandidateId(), sectionStatusUpdateRequest.getSection(), sectionStatusUpdateRequest.getStatus());
        
        CandidateVerificationDTO verification = verificationService.updateSectionStatus(
        		sectionStatusUpdateRequest.getCandidateId(), sectionStatusUpdateRequest.getSection(), sectionStatusUpdateRequest.getStatus());
        
        return ResponseEntity.ok(verification);
    }
    
    @Operation(summary = "Submit verification for review")
    @PostMapping("/submit")
    public ResponseEntity<CandidateVerificationDTO> submitVerification(
    		@PathVariable Long candidateId
            ) throws ValidationException {
        
        log.info("POST /api/verification/submit?candidateId={} by {}", candidateId);
        
        CandidateVerificationDTO verification = verificationService.submitForVerification(candidateId);
        return ResponseEntity.ok(verification);
    }
    
    @Operation(summary = "Create new verification")
    @PostMapping("/create")
    public ResponseEntity<CandidateVerificationDTO> createVerification(
    		@PathVariable Long candidateId,
            @Valid @RequestBody CandidateVerificationDTO request,
            @AuthenticationPrincipal String username) throws ValidationException {
        
        log.info("POST /api/verification/create?candidateId={} by {}", candidateId, username);
        
        CandidateVerificationDTO verification = verificationService.createVerification(candidateId, request);
        return ResponseEntity.ok(verification);
    }
    /*
    @Operation(summary = "Get section details")
    @GetMapping("/section")
    public ResponseEntity<VerificationSectionDTO> getSectionDetails(
    		@PathVariable Long candidateId,
            @RequestParam String section,
            @AuthenticationPrincipal String username) {
        
        log.info("GET /api/verification/section?candidateId={}&section={} by {}", candidateId, section, username);
        
        CandidateVerificationDTO verification = verificationService.getCandidateVerification(candidateId);
        VerificationSectionDTO sectionDTO = verification.getSections().get(section);
        
        if (sectionDTO == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(sectionDTO);
    }
    */
    @Operation(
            summary = "Get all verification cases for a candidate",
            description = "Retrieve paginated list of verification cases for a specific candidate with filtering options"
        )
        @GetMapping("/cases")
        public ResponseEntity<VerificationCaseResponseDTO> getCandidateVerificationCases(
                @PathVariable Long candidateId,
                @RequestParam(required = false) String search,
                
                @RequestParam(required = false) String status,
                
                @RequestParam(required = false) String company,
                
                @RequestParam(required = false) String verificationType,
                
                @RequestParam(defaultValue = "0") Integer page,
                
                @RequestParam(defaultValue = "10") Integer size,
                
                @RequestParam(defaultValue = "createdAt") String sortBy,
                
                @RequestParam(defaultValue = "desc") String sortDirection) {
            
            log.info("GET request for verification cases of candidateId: {}", candidateId);
            
            // Build filter DTO
            VerificationCaseFilterDTO filterDTO = VerificationCaseFilterDTO.builder()
                .candidateId(candidateId)
                .searchTerm(search)
                .status(status != null ? CaseStatus.fromString(status) : null)
                .companyName(company)
                .verificationType(verificationType)
                .page(page)
                .pageSize(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
            
            VerificationCaseResponseDTO response = verificationCaseService.getCandidateVerificationCases(filterDTO);
            
            return ResponseEntity.ok(response);
        }
        
        @Operation(
            summary = "Get a specific verification case",
            description = "Retrieve details of a specific verification case for a candidate"
        )
        
        @GetMapping("/{caseId}")
        public ResponseEntity<VerificationCaseDTO> getCandidateVerificationCase(
                @PathVariable Long candidateId,
                @PathVariable Long caseId) {
            
            log.info("GET request for verification case {} of candidateId: {}", caseId, candidateId);
            
            VerificationCaseDTO verificationCase = verificationCaseService.getCandidateVerificationCase(candidateId, caseId);
            
            return ResponseEntity.ok(verificationCase);
        }
        
        @Operation(
            summary = "Get verification case statistics",
            description = "Retrieve statistics of verification cases for a candidate"
        )
        
        @GetMapping("/statistics")
        public ResponseEntity<CaseStatisticsDTO> getCaseStatistics(
                @PathVariable Long candidateId) {
            
            log.info("GET request for case statistics of candidateId: {}", candidateId);
            
            CaseStatisticsDTO statistics = verificationCaseService.getCaseStatistics(candidateId);
            
            return ResponseEntity.ok(statistics);
        }
        
        @Operation(
            summary = "Get all verification cases for a candidate (with filters in body)",
            description = "Retrieve verification cases with advanced filtering options"
        )
        @PostMapping("/search")
        public ResponseEntity<VerificationCaseResponseDTO> searchCandidateVerificationCases(
                @PathVariable Long candidateId,
                @Valid @RequestBody VerificationCaseFilterDTO filterDTO) {
            
            log.info("POST request to search verification cases for candidateId: {}", candidateId);
            
            // Ensure the candidateId in path matches the filter
            filterDTO.setCandidateId(candidateId);
            
            VerificationCaseResponseDTO response = verificationCaseService.getCandidateVerificationCases(filterDTO);
            
            return ResponseEntity.ok(response);
        }
}