package com.org.bgv.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.service.VendorDashboardService;
import com.org.bgv.vendor.dto.VendorVerificationCheckDTO;
import com.org.bgv.vendor.service.VerificationCheckService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/vendor/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class VendorDashboardController {
	
   private final VendorDashboardService vendorDashboardService;
   private final VerificationCheckService verificationCheckService;
    
   
   @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardData(@RequestParam Long vendorId) {
        Map<String, Object> dashboardData = vendorDashboardService.getVendorDashboardData(vendorId);
        return ResponseEntity.ok(dashboardData);
    }
    
    @GetMapping("/vendor/{vendorId}/check/{checkId}")
    public ResponseEntity<CustomApiResponse<VendorVerificationCheckDTO>> getCheckData(
            @PathVariable Long vendorId,
            @PathVariable Long checkId) {
        try {
            VendorVerificationCheckDTO verificationCheckDTO = verificationCheckService.getVerificationCheck(checkId, vendorId);
            return ResponseEntity.ok(CustomApiResponse.success(
                "Verification check retrieved successfully", 
                verificationCheckDTO, 
                HttpStatus.OK
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Failed to retrieve verification check: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    @PutMapping("/vendor/{vendorId}/check/{checkId}/status")
    public ResponseEntity<CustomApiResponse<Void>> updateCheckStatus(
            @PathVariable Long vendorId,
            @PathVariable Long checkId,
            @RequestParam String status,
            @RequestParam(required = false) String notes) {
        try {
            log.info("Updating check {} status to {} for vendor {}", checkId, status, vendorId);
            
            verificationCheckService.updateCheckStatus(checkId, vendorId, status, notes);
            
            return ResponseEntity.ok(CustomApiResponse.success(
                "Check status updated successfully", 
                null, 
                HttpStatus.OK
            ));
        } catch (RuntimeException e) {
            log.warn("Failed to update check status: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            log.error("Error updating check status: {}", e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Failed to update check status", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    
    @PostMapping("/vendor/{vendorId}/check/{checkId}/notes")
    public ResponseEntity<CustomApiResponse<Void>> addNoteToCheck(
            @PathVariable Long vendorId,
            @PathVariable Long checkId,
            @RequestParam String content,
            @RequestParam(required = false, defaultValue = "internal") String noteType) {
        try {
            log.info("Adding note to check {} for vendor {}", checkId, vendorId);
            
            verificationCheckService.addVendorNote(checkId, vendorId, content, noteType);
            
            return ResponseEntity.ok(CustomApiResponse.success(
                "Note added successfully", 
                null, 
                HttpStatus.OK
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            log.error("Error adding note to check: {}", e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Failed to add note", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    
}
