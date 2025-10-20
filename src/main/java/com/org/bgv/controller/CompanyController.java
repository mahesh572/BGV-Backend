package com.org.bgv.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.ApiResponse;
import com.org.bgv.common.Status;
import com.org.bgv.company.dto.CompanyRegistrationRequestDTO;
import com.org.bgv.company.dto.CompanyRegistrationResponse;
import com.org.bgv.company.dto.PersonDTO;
import com.org.bgv.entity.Company;
import com.org.bgv.service.CompanyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

	private static final Logger log = LoggerFactory.getLogger(CompanyController.class);
	
	private final CompanyService companyService;

	@PostMapping("/register")
    public ResponseEntity<ApiResponse<CompanyRegistrationResponse>> registerCompany(
    		@RequestBody CompanyRegistrationRequestDTO request) {
        
        log.info("Received company registration request for: {}", request.getCompanyName());
        
        try {
            CompanyRegistrationResponse response = companyService.registerCompany(request);
            return ResponseEntity.ok(ApiResponse.success(
                "Company registered successfully", 
                response, 
                HttpStatus.CREATED
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Validation error in company registration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
                    
        } catch (Exception e) {
            log.error("Unexpected error during company registration: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(
                        "Internal server error during registration", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @GetMapping("/check-registration-number")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkRegistrationNumber(
            @RequestParam String registrationNumber) {
        
        try {
            boolean exists = companyService.isRegistrationNumberExists(registrationNumber);
            Map<String, Boolean> response = Map.of("exists", exists);
            String message = exists ? "Registration number already exists" : "Registration number available";
            
            return ResponseEntity.ok(ApiResponse.success(message, response, HttpStatus.OK));
            
        } catch (Exception e) {
            log.error("Error checking registration number: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(
                        "Failed to check registration number: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @GetMapping("/check-admin-email")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkAdminEmail(
            @RequestParam String email) {
        
        try {
            boolean exists = companyService.isAdminEmailExists(email);
            Map<String, Boolean> response = Map.of("exists", exists);
            String message = exists ? "Email already exists" : "Email available";
            
            return ResponseEntity.ok(ApiResponse.success(message, response, HttpStatus.OK));
            
        } catch (Exception e) {
            log.error("Error checking admin email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(
                        "Failed to check email: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                "Company Registration Service is running", 
                "Service is healthy", 
                HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(
                        "Service health check failed", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
 // Get all companies with pagination and filtering
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "companyName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String status) {

        log.info("Fetching all companies - page: {}, size: {}, sort: {}, direction: {}, search: {}, industry: {}, status: {}",
                page, size, sortBy, sortDirection, search, industry, status);

        try {
            Pageable pageable = PageRequest.of(page, size, 
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
            
            Map<String, Object> response = companyService.getAllCompanies(pageable, search, industry, status);
            
            return ResponseEntity.ok(ApiResponse.success(
                "Companies retrieved successfully", 
                response, 
                HttpStatus.OK
            ));
            
        } catch (Exception e) {
            log.error("Error fetching companies: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(
                        "Failed to fetch companies: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // Get company by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Company>> getCompanyById(@PathVariable Long id) {
        log.info("Fetching company with ID: {}", id);
        
        try {
            Company company = companyService.getCompanyById(id);
            return ResponseEntity.ok(ApiResponse.success(
                "Company retrieved successfully", 
                company, 
                HttpStatus.OK
            ));
        } catch (RuntimeException e) {
            log.warn("Company not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("Error fetching company with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(
                        "Failed to fetch company: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // Get companies count for dashboard
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCompaniesCount() {
        log.info("Fetching companies count");
        
        try {
            Map<String, Long> counts = companyService.getCompaniesCount();
            return ResponseEntity.ok(ApiResponse.success(
                "Companies count retrieved successfully", 
                counts, 
                HttpStatus.OK
            ));
        } catch (Exception e) {
            log.error("Error fetching companies count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(
                        "Failed to fetch companies count: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // Update company status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Company>> updateCompanyStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        
        log.info("Updating company status - ID: {}, status: {}", id, status);
        
        try {
            Company company = companyService.updateCompanyStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success(
                "Company status updated successfully", 
                company, 
                HttpStatus.OK
            ));
        } catch (RuntimeException e) {
            log.warn("Company not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("Error updating company status for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(
                        "Failed to update company status: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    // Employee related 
    
    @PostMapping("/{companyId}/employee")
    public ResponseEntity<ApiResponse<Boolean>> addEmployee(
    		@PathVariable Long companyId,
            @RequestBody PersonDTO employeeDTO,
            @RequestParam(defaultValue = "ACTIVE") String status) {
        
        log.info("Received employee addition request for company ID: {}, employee: {}", 
                 companyId, employeeDTO.getEmail());
        
        try {
            Boolean response = companyService.addPerson(companyId, employeeDTO,Status.USER_TYPE_COMPANY);
            return ResponseEntity.ok(ApiResponse.success(
                "Employee added successfully", 
                response, 
                HttpStatus.CREATED
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Validation error in employee addition: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
                    
        } catch (RuntimeException e) {
            log.error("Business logic error during employee addition: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.CONFLICT));
                    
        } catch (Exception e) {
            log.error("Unexpected error during employee addition: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(
                        "Internal server error while adding employee", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @PostMapping("/{companyId}/candidate")
    public ResponseEntity<ApiResponse<Boolean>> addCandidate(
    		@PathVariable Long companyId,
            @RequestBody PersonDTO candidateDTO
            ) {
        
        log.info("Received Candidate addition request for company ID: {}, Candidate: {}", 
                 companyId, candidateDTO.getEmail());
        
        try {
            Boolean response = companyService.addPerson(companyId, candidateDTO,"CANDIDATE");
            return ResponseEntity.ok(ApiResponse.success(
                "Candidate added successfully", 
                response, 
                HttpStatus.CREATED
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Validation error in candidate addition: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
                    
        } catch (RuntimeException e) {
            log.error("Business logic error during candidate addition: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.CONFLICT));
                    
        } catch (Exception e) {
            log.error("Unexpected error during candidate addition: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(
                        "Internal server error while adding candidate", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    
}
