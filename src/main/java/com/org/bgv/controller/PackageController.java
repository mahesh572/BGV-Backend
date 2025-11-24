package com.org.bgv.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.dto.PackageRequestDTO;
import com.org.bgv.dto.PackageResponse;
import com.org.bgv.service.CandidateService;
import com.org.bgv.service.CompanyService;
import com.org.bgv.service.PackageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/package")
@RequiredArgsConstructor
public class PackageController {
	
	private final PackageService packageService;

    @PostMapping
    public ResponseEntity<CustomApiResponse<PackageResponse>> createPackage(
            @RequestBody PackageRequestDTO request) {
        
        log.info("Received package creation request for: {}", request.getName());
        
        try {
            PackageResponse response = packageService.createPackage(request);
            return ResponseEntity.ok(CustomApiResponse.success(
                "Package created successfully", 
                response, 
                HttpStatus.CREATED
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Validation error in package creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
                    
        } catch (Exception e) {
            log.error("Unexpected error during package creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Internal server error during package creation", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<PackageResponse>> getPackageById(@PathVariable Long id) {
        log.info("Fetching package by id: {}", id);
        
        try {
            PackageResponse response = packageService.getPackageById(id);
            return ResponseEntity.ok(CustomApiResponse.success(
                "Package retrieved successfully", 
                response, 
                HttpStatus.OK
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Package not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
                    
        } catch (Exception e) {
            log.error("Unexpected error while fetching package: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Internal server error while fetching package", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CustomApiResponse<PackageResponse>> getPackageByCode(@PathVariable String code) {
        log.info("Fetching package by code: {}", code);
        
        try {
            PackageResponse response = packageService.getPackageByCode(code);
            return ResponseEntity.ok(CustomApiResponse.success(
                "Package retrieved successfully", 
                response, 
                HttpStatus.OK
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Package not found with code: {}", code);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
                    
        } catch (Exception e) {
            log.error("Unexpected error while fetching package: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Internal server error while fetching package", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @GetMapping
    public ResponseEntity<CustomApiResponse<List<PackageResponse>>> getAllPackages() {
        log.info("Fetching all packages");
        
        try {
            List<PackageResponse> response = packageService.getAllPackages();
            return ResponseEntity.ok(CustomApiResponse.success(
                "Packages retrieved successfully", 
                response, 
                HttpStatus.OK
            ));
            
        } catch (Exception e) {
            log.error("Unexpected error while fetching packages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Internal server error while fetching packages", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<CustomApiResponse<List<PackageResponse>>> getActivePackages() {
        log.info("Fetching active packages");
        
        try {
            List<PackageResponse> response = packageService.getActivePackages();
            return ResponseEntity.ok(CustomApiResponse.success(
                "Active packages retrieved successfully", 
                response, 
                HttpStatus.OK
            ));
            
        } catch (Exception e) {
            log.error("Unexpected error while fetching active packages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Internal server error while fetching active packages", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @GetMapping("/customizable")
    public ResponseEntity<CustomApiResponse<List<PackageResponse>>> getCustomizablePackages() {
        log.info("Fetching customizable packages");
        
        try {
            List<PackageResponse> response = packageService.getCustomizablePackages();
            return ResponseEntity.ok(CustomApiResponse.success(
                "Customizable packages retrieved successfully", 
                response, 
                HttpStatus.OK
            ));
            
        } catch (Exception e) {
            log.error("Unexpected error while fetching customizable packages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Internal server error while fetching customizable packages", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse<PackageResponse>> updatePackage(
            @PathVariable Long id, 
            @RequestBody PackageRequestDTO request) {
        
        log.info("Updating package with id: {}", id);
        
        try {
            PackageResponse response = packageService.updatePackage(id, request);
            return ResponseEntity.ok(CustomApiResponse.success(
                "Package updated successfully", 
                response, 
                HttpStatus.OK
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Validation error in package update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
                    
        } catch (Exception e) {
            log.error("Unexpected error during package update: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Internal server error during package update", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse<Void>> deletePackage(@PathVariable Long id) {
        log.info("Deleting package with id: {}", id);
        
        try {
            packageService.deletePackage(id);
            return ResponseEntity.ok(CustomApiResponse.success(
                "Package deleted successfully", 
                null, 
                HttpStatus.OK
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Package not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
                    
        } catch (Exception e) {
            log.error("Unexpected error during package deletion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Internal server error during package deletion", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CustomApiResponse<PackageResponse>> activatePackage(@PathVariable Long id) {
        log.info("Activating package with id: {}", id);
        
        try {
            PackageResponse response = packageService.activatePackage(id);
            return ResponseEntity.ok(CustomApiResponse.success(
                "Package activated successfully", 
                response, 
                HttpStatus.OK
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Package not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
                    
        } catch (Exception e) {
            log.error("Unexpected error during package activation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Internal server error during package activation", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CustomApiResponse<PackageResponse>> deactivatePackage(@PathVariable Long id) {
        log.info("Deactivating package with id: {}", id);
        
        try {
            PackageResponse response = packageService.deactivatePackage(id);
            return ResponseEntity.ok(CustomApiResponse.success(
                "Package deactivated successfully", 
                response, 
                HttpStatus.OK
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Package not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
                    
        } catch (Exception e) {
            log.error("Unexpected error during package deactivation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Internal server error during package deactivation", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
	
	

}
