package com.org.bgv.controller;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.CategoryDocumentsDto;
import com.org.bgv.common.EmployerPackageRequest;
import com.org.bgv.common.EmployerPackageResponse;
import com.org.bgv.common.PackageDTO;
import com.org.bgv.common.PackageRequest;
import com.org.bgv.constants.EmployerPackageStatus;
import com.org.bgv.service.EmployerPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/employer-packages")
@RequiredArgsConstructor
public class EmployerPackageController {
    
    private final EmployerPackageService employerPackageService;
    
    @PostMapping
    public ResponseEntity<CustomApiResponse<EmployerPackageResponse>> createEmployerPackage(
            @Valid @RequestBody EmployerPackageRequest request) {
        try {
            EmployerPackageResponse response = employerPackageService.createEmployerPackage(request);
            return ResponseEntity.ok(CustomApiResponse.success("Employer package created successfully", response, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to create employer package: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<PackageDTO>> getEmployerPackage(@PathVariable Long id) {
        try {
        	log.info("employer packages:::::::GET:::::::getEmployerPackage:::::{}",id);
        	PackageDTO response = employerPackageService.getEmployerPackage(id);
            return ResponseEntity.ok(CustomApiResponse.success("Employer package retrieved successfully", response, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve employer package: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    
    // Get all packages assigned to specific company...
    @GetMapping("/company/{companyId}")
    public ResponseEntity<CustomApiResponse<List<EmployerPackageResponse>>> getEmployerPackagesByCompany(
            @PathVariable Long companyId) {
        try {
            List<EmployerPackageResponse> responses = employerPackageService.getEmployerPackagesByCompany(companyId);
            return ResponseEntity.ok(CustomApiResponse.success("Employer packages retrieved successfully", responses, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve employer packages: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/{id}/company/{companyId}")
    public ResponseEntity<CustomApiResponse<EmployerPackageResponse>> getEmployerPackageByCompany(
            @PathVariable Long id, @PathVariable Long companyId) {
        try {
            EmployerPackageResponse response = employerPackageService.getEmployerPackageByCompany(id, companyId);
            return ResponseEntity.ok(CustomApiResponse.success("Employer package retrieved successfully", response, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve employer package: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @PostMapping("/{id}/company/{companyId}/activate")
    public ResponseEntity<CustomApiResponse<EmployerPackageResponse>> activateEmployerPackage(
            @PathVariable Long id, @PathVariable Long companyId) {
        try {
            EmployerPackageResponse response = employerPackageService.activateEmployerPackage(id, companyId);
            return ResponseEntity.ok(CustomApiResponse.success("Employer package activated successfully", response, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to activate employer package: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @PostMapping("/{id}/company/{companyId}/suspend")
    public ResponseEntity<CustomApiResponse<EmployerPackageResponse>> suspendEmployerPackage(
            @PathVariable Long id, @PathVariable Long companyId) {
        try {
            EmployerPackageResponse response = employerPackageService.suspendEmployerPackage(id, companyId);
            return ResponseEntity.ok(CustomApiResponse.success("Employer package suspended successfully", response, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to suspend employer package: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @PostMapping("/{id}/company/{companyId}/renew")
    public ResponseEntity<CustomApiResponse<EmployerPackageResponse>> renewEmployerPackage(
            @PathVariable Long id, @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime validUntil) {
        try {
            EmployerPackageResponse response = employerPackageService.renewEmployerPackage(id, companyId, validUntil);
            return ResponseEntity.ok(CustomApiResponse.success("Employer package renewed successfully", response, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to renew employer package: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @PutMapping("/{id}/company/{companyId}/status")
    public ResponseEntity<CustomApiResponse<EmployerPackageResponse>> updateEmployerPackageStatus(
            @PathVariable Long id, @PathVariable Long companyId, 
            @RequestParam EmployerPackageStatus status) {
        try {
            EmployerPackageResponse response = employerPackageService.updateEmployerPackageStatus(id, companyId, status);
            return ResponseEntity.ok(CustomApiResponse.success("Employer package status updated successfully", response, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update employer package status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/company/{companyId}/active")
    public ResponseEntity<CustomApiResponse<List<EmployerPackageResponse>>> getActiveEmployerPackages(
            @PathVariable Long companyId) {
        try {
            List<EmployerPackageResponse> responses = employerPackageService.getActiveEmployerPackages(companyId);
            return ResponseEntity.ok(CustomApiResponse.success("Active employer packages retrieved successfully", responses, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve active employer packages: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @DeleteMapping("/{id}/company/{companyId}")
    public ResponseEntity<CustomApiResponse<Void>> deleteEmployerPackage(
            @PathVariable Long id, @PathVariable Long companyId) {
        try {
            employerPackageService.deleteEmployerPackage(id, companyId);
            return ResponseEntity.ok(CustomApiResponse.success("Employer package deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete employer package: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/company/{companyId}/status/{status}")
    public ResponseEntity<CustomApiResponse<List<EmployerPackageResponse>>> getEmployerPackagesByStatus(
            @PathVariable Long companyId, @PathVariable EmployerPackageStatus status) {
        try {
            List<EmployerPackageResponse> responses = employerPackageService.getEmployerPackagesByStatus(companyId, status);
            return ResponseEntity.ok(CustomApiResponse.success("Employer packages retrieved successfully", responses, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve employer packages: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @PutMapping("/{packageId}")
    public ResponseEntity<CustomApiResponse<List<?>>> updateEmployerPackageDocuments(@PathVariable Long packageId, 
            @Valid @RequestBody PackageRequest request) {
    	try {
    		log.info("employer packages:::::::update::::packageId:{}",packageId);
    	employerPackageService.updateEmployerPackageDocuments(packageId, request);
    	 return ResponseEntity.ok(CustomApiResponse.success("Employer packages retrieved successfully", null, HttpStatus.OK));
    	} catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve employer packages: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/{employerpackageId}/documents")
    public ResponseEntity<CustomApiResponse<List<CategoryDocumentsDto>>> getPackageDocuments(
            @PathVariable Long employerpackageId) {
        
        try {
            log.info("Getting documents for employerpackageId: {}", employerpackageId);
            List<CategoryDocumentsDto> result = employerPackageService
                    .getPackageDocumentsByCategory(employerpackageId);
            
            return ResponseEntity.ok(
                    CustomApiResponse.<List<CategoryDocumentsDto>>success(
                            "Package documents retrieved successfully",
                            result,
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get documents for packageId: {}", employerpackageId, e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.<List<CategoryDocumentsDto>>failure(
                            "Failed to retrieve package documents: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
}