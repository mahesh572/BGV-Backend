package com.org.bgv.candidate.controller;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.org.bgv.dto.BasicDetailsDTO;
import com.org.bgv.service.ProfileService;

@RestController
@RequestMapping("/api/basicdetails/candidate")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Basic Details", description = "APIs for managing candidate basic details")
public class BasicDetailsController {
    
    private final ProfileService profileService;
    /*
    @Operation(summary = "Get candidate basic details")
    @GetMapping("/{candidateId}/basic-details")
    public ResponseEntity<BasicDetailsDTO> getBasicDetails(
            @PathVariable Long candidateId,
            @AuthenticationPrincipal String username) {
        
        log.info("GET /api/candidate/{}/basic-details requested by {}", candidateId, username);
        
        BasicDetailsDTO basicDetails = profileService.getBasicDetails(candidateId);
        return ResponseEntity.ok(basicDetails);
    }
    */
    
    /*
    @Operation(summary = "Save/update candidate basic details")
    @PostMapping("/{candidateId}/basic-details")
    public ResponseEntity<BasicDetailsDTO> saveBasicDetails(
            @PathVariable Long candidateId,
            @Valid @RequestBody BasicDetailsDTO request,
            @AuthenticationPrincipal String username) {
        
        log.info("POST /api/candidate/{}/basic-details by {}", candidateId, username);
        
        // Ensure candidate ID matches
        request.setCandidateId(candidateId);
        
        BasicDetailsDTO savedDetails = profileService.saveBasicDetails(candidateId, request);
        return ResponseEntity.ok(savedDetails);
    }
    */
}
