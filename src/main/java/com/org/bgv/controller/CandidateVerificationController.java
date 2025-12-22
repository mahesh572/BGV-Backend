package com.org.bgv.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.candidate.dto.CandidateVerificationDTO;
import com.org.bgv.candidate.dto.VerificationSectionDTO;
import com.org.bgv.candidate.service.VerificationService;
import com.org.bgv.constants.SectionStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Candidate Verification", description = "APIs for candidate verification management")
public class CandidateVerificationController {
    
    private final VerificationService verificationService;
    
    @Operation(summary = "Get candidate verification details")
    @GetMapping("/candidate")
    public ResponseEntity<CandidateVerificationDTO> getCandidateVerification(
            @RequestParam Long candidateId,
            @AuthenticationPrincipal String username) {
        
        log.info("GET /api/verification/candidate?candidateId={} requested by {}", candidateId, username);
        
        CandidateVerificationDTO verification = verificationService.getCandidateVerification(candidateId);
        return ResponseEntity.ok(verification);
    }
    
    @Operation(summary = "Update section status")
    @PutMapping("/section/status")
    public ResponseEntity<CandidateVerificationDTO> updateSectionStatus(
            @RequestParam Long candidateId,
            @RequestParam String section,
            @RequestParam SectionStatus status,
            @RequestBody(required = false) Map<String, Object> data,
            @AuthenticationPrincipal String username) throws ValidationException {
        
        log.info("PUT /api/verification/section/status?candidateId={}&section={}&status={} by {}", 
                candidateId, section, status, username);
        
        CandidateVerificationDTO verification = verificationService.updateSectionStatus(
            candidateId, section, status, data);
        
        return ResponseEntity.ok(verification);
    }
    
    @Operation(summary = "Submit verification for review")
    @PostMapping("/submit")
    public ResponseEntity<CandidateVerificationDTO> submitVerification(
            @RequestParam Long candidateId,
            @AuthenticationPrincipal String username) throws ValidationException {
        
        log.info("POST /api/verification/submit?candidateId={} by {}", candidateId, username);
        
        CandidateVerificationDTO verification = verificationService.submitForVerification(candidateId);
        return ResponseEntity.ok(verification);
    }
    
    @Operation(summary = "Create new verification")
    @PostMapping("/create")
    public ResponseEntity<CandidateVerificationDTO> createVerification(
            @RequestParam Long candidateId,
            @Valid @RequestBody CandidateVerificationDTO request,
            @AuthenticationPrincipal String username) throws ValidationException {
        
        log.info("POST /api/verification/create?candidateId={} by {}", candidateId, username);
        
        CandidateVerificationDTO verification = verificationService.createVerification(candidateId, request);
        return ResponseEntity.ok(verification);
    }
    
    @Operation(summary = "Get section details")
    @GetMapping("/section")
    public ResponseEntity<VerificationSectionDTO> getSectionDetails(
            @RequestParam Long candidateId,
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
}