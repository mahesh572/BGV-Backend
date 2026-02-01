package com.org.bgv.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.candidate.CandidateSearchRequest;
import com.org.bgv.candidate.dto.CreateCandidateRequest;
import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.common.CandidateDTO;
import com.org.bgv.common.CandidateDetailsDTO;
import com.org.bgv.common.PaginationResponse;
import com.org.bgv.common.RemoveUsersRequest;
import com.org.bgv.common.RoleConstants;
import com.org.bgv.common.SortingRequest;
import com.org.bgv.common.Status;
import com.org.bgv.company.dto.CompanyRegistrationRequestDTO;
import com.org.bgv.company.dto.CompanyRegistrationResponse;
import com.org.bgv.company.dto.EmployeeDTO;
import com.org.bgv.company.dto.PersonDTO;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.Constants;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.User;
import com.org.bgv.role.dto.RoleResponse;
import com.org.bgv.service.CandidateService;
import com.org.bgv.service.CompanyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

	private static final Logger log = LoggerFactory.getLogger(CompanyController.class);
	
	private final CompanyService companyService;
	private final CandidateService candidateService;

    

    @GetMapping("/check-admin-email")
    public ResponseEntity<CustomApiResponse<Map<String, Boolean>>> checkAdminEmail(
            @RequestParam String email) {
        
        try {
            boolean exists = companyService.isAdminEmailExists(email);
            Map<String, Boolean> response = Map.of("exists", exists);
            String message = exists ? "Email already exists" : "Email available";
            
            return ResponseEntity.ok(CustomApiResponse.success(message, response, HttpStatus.OK));
            
        } catch (Exception e) {
            log.error("Error checking admin email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Failed to check email: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<CustomApiResponse<String>> healthCheck() {
        try {
            return ResponseEntity.ok(CustomApiResponse.success(
                "Company Registration Service is running", 
                "Service is healthy", 
                HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Service health check failed", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
 // Get all companies with pagination and filtering
    /*
    @GetMapping
    public ResponseEntity<CustomApiResponse<Map<String, Object>>> getAllCompanies(
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
            
            return ResponseEntity.ok(CustomApiResponse.success(
                "Companies retrieved successfully", 
                response, 
                HttpStatus.OK
            ));
            
        } catch (Exception e) {
            log.error("Error fetching companies: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Failed to fetch companies: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
*/
    // Get company by ID
    

    // Get companies count for dashboard
    @GetMapping("/count")
    public ResponseEntity<CustomApiResponse<Map<String, Long>>> getCompaniesCount() {
        log.info("Fetching companies count");
        
        try {
            Map<String, Long> counts = companyService.getCompaniesCount();
            return ResponseEntity.ok(CustomApiResponse.success(
                "Companies count retrieved successfully", 
                counts, 
                HttpStatus.OK
            ));
        } catch (Exception e) {
            log.error("Error fetching companies count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Failed to fetch companies count: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // Update company status
    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomApiResponse<Company>> updateCompanyStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        
        log.info("Updating company status - ID: {}, status: {}", id, status);
        
        try {
            Company company = companyService.updateCompanyStatus(id, status);
            return ResponseEntity.ok(CustomApiResponse.success(
                "Company status updated successfully", 
                company, 
                HttpStatus.OK
            ));
        } catch (RuntimeException e) {
            log.warn("Company not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("Error updating company status for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Failed to update company status: " + e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    // Employee related 
    /*
    @PostMapping("/{companyId}/employee")
    public ResponseEntity<CustomApiResponse<Boolean>> addEmployee(
    		@PathVariable Long companyId,
            @RequestBody EmployeeDTO employeeDTO,
            @RequestParam(defaultValue = "ACTIVE") String status) {
        
        log.info("Received employee addition request for company ID: {}, employee: {}", 
                 companyId, employeeDTO.getEmail());
        
        try {
            Boolean response = companyService.addEmployee(companyId, employeeDTO,Status.USER_TYPE_COMPANY);
            return ResponseEntity.ok(CustomApiResponse.success(
                "Employee added successfully", 
                response, 
                HttpStatus.CREATED
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Validation error in employee addition: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
                    
        } catch (RuntimeException e) {
            log.error("Business logic error during employee addition: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.CONFLICT));
                    
        } catch (Exception e) {
            log.error("Unexpected error during employee addition: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Internal server error while adding employee", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
*/
    
   
    
    @PostMapping("/{companyId}/candidate")
    public ResponseEntity<CustomApiResponse<Boolean>> addCandidate(
    		@PathVariable Long companyId,
            @RequestBody CreateCandidateRequest candidateDTO
            ) {
        
        log.info("Received Candidate addition request for company ID: {}, Candidate: {}", 
                 companyId, candidateDTO.getEmail());
        
        try {
        	candidateDTO.setCompanyId(companyId);
        	// candidateDTO.setSourceType(Constants.CANDIDATE_SOURCE_EMPLOYER);
            
            Boolean response = candidateService.addCandidate(candidateDTO);
            return ResponseEntity.ok(CustomApiResponse.success(
                "Candidate added successfully", 
                response, 
                HttpStatus.CREATED
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Validation error in candidate addition: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
                    
        } catch (RuntimeException e) {
            log.error("Business logic error during candidate addition: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.CONFLICT));
                    
        } catch (Exception e) {
            log.error("Unexpected error during candidate addition: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Internal server error while adding candidate", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    
    @PostMapping("/{companyId}/users")
    public ResponseEntity<CustomApiResponse<String>> removeUsersFromCompany(
            @PathVariable Long companyId,
            @Valid @RequestBody RemoveUsersRequest request) {
        
        log.info("Removing users from company {}: {}", companyId, request.getUserIds());
        
        try {
        	companyService.removeUserFromCompany(request.getUserIds(), companyId);
            
            return ResponseEntity.ok(
            		CustomApiResponse.success(
                    String.format("Successfully removed %d users from company", request.getUserIds().size()),
                    "",
                    HttpStatus.OK
                )
            );
            
        } catch (RuntimeException e) {
            log.error("Error removing users from company {}: {}", companyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CustomApiResponse.failure(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
 
    @PostMapping("/{companyId}/candidates/search")
    public ResponseEntity<CustomApiResponse<PaginationResponse<CandidateDTO>>> searchCandidates(
    		 @PathVariable Long companyId,
             @RequestBody CandidateSearchRequest searchRequest) {

        log.info("Candidate search request received");

        try {

            /* ---------- Role based company scoping ---------- */
            if (SecurityUtils.hasAnyRole(
                    RoleConstants.ROLE_COMAPNY_ADMINISTRATOR
                    )) {

               // Long companyId = SecurityUtils.getCurrentUserCompanyId();
                log.info("Applying company scope. companyId={}", companyId);

                searchRequest.setCompanyId(companyId);
            }

            /* ---------- Default sorting ---------- */
            if (searchRequest.getSorting() == null) {
                searchRequest.setSorting(SortingRequest.builder()
                        .sortBy("createdAt")
                        .sortDirection("desc")
                        .build());
            }

            /* ---------- Remove empty filters ---------- */
            if (searchRequest.getFilters() != null) {
                searchRequest.getFilters().removeIf(filter ->
                        filter.getField() == null ||
                        filter.getField().isEmpty() ||
                        !Boolean.TRUE.equals(filter.getIsSelected())
                );
            }

            PaginationResponse<CandidateDTO> result =
                    candidateService.searchCandidates(searchRequest);

            log.info("Candidate search successful. totalElements={}",
                    result.getPagination().getTotalElements());

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Candidates retrieved successfully",
                            result,
                            HttpStatus.OK
                    )
            );

        } catch (RuntimeException e) {
            log.warn("Candidate search failed: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));

        } catch (Exception e) {
            log.error("Unexpected error during candidate search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Unexpected error while searching candidates",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    
    @GetMapping("/{companyId}/candidate/{candidateId}")
	 public ResponseEntity<CustomApiResponse<CandidateDetailsDTO>> getCandidateDetails(
			 @PathVariable Long companyId,
	         @PathVariable Long candidateId) {
	     
		// Long companyId = SecurityUtils.getCurrentUserCompanyId();
		 
	     try {
	         log.info("Request received for candidate details with candidateId: {}", candidateId);
	         
	         if (candidateId == null || candidateId <= 0) {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                     .body(CustomApiResponse.failure(
	                         "Invalid candidate ID. Candidate ID must be a positive number", 
	                         HttpStatus.BAD_REQUEST));
	         }
	         
	         CandidateDetailsDTO candidateDetails = candidateService.getCandidateDetails(companyId,candidateId);
	         
	         if (candidateDetails == null) {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                     .body(CustomApiResponse.failure(
	                         "Candidate not found with ID: " + candidateId, 
	                         HttpStatus.NOT_FOUND));
	         }
	         
	         return ResponseEntity.ok(
	             CustomApiResponse.success(
	                 "Candidate details fetched successfully", 
	                 candidateDetails, 
	                 HttpStatus.OK
	             )
	         );
	         
	     } catch (EntityNotFoundException e) {
	         log.error("Candidate not found with ID: {}", candidateId, e);
	         return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                 .body(CustomApiResponse.failure(
	                     "Candidate not found: " + e.getMessage(), 
	                     HttpStatus.NOT_FOUND));
	                     
	     } catch (IllegalArgumentException e) {
	         log.error("Invalid request for candidate ID: {}", candidateId, e);
	         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                 .body(CustomApiResponse.failure(
	                     "Invalid request: " + e.getMessage(), 
	                     HttpStatus.BAD_REQUEST));
	                     
	     } catch (Exception e) {
	         log.error("Unexpected error fetching candidate details for ID: {}", candidateId, e);
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                 .body(CustomApiResponse.failure(
	                     "Internal server error while fetching candidate details", 
	                     HttpStatus.INTERNAL_SERVER_ERROR));
	     }
	 }
    
    @PostMapping("/{companyId}/logo")
    public ResponseEntity<CustomApiResponse<String>> uploadLogo(
            @PathVariable Long companyId,
            @RequestParam("file") MultipartFile file) {

        log.info("Uploading logo for companyId={}", companyId);

        try {
            companyService.uploadLogo(companyId, file);

            log.info("Logo uploaded successfully for companyId={}", companyId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Company logo uploaded successfully",
                            "",
                            HttpStatus.OK
                    )
            );

        } catch (RuntimeException e) {
            log.error("Error uploading logo for companyId={}: {}", companyId, e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        } catch (Exception e) {
            log.error("Unexpected error while uploading logo for companyId={}", companyId, e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Unexpected error while uploading company logo",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @GetMapping("/{companyId}/logo")
    public ResponseEntity<CustomApiResponse<Map<String, Object>>> getLogo(
            @PathVariable Long companyId) {

        Map<String, Object> response = companyService.getOrganizationLogo(companyId);

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Logo fetched successfully",
                        response,
                        HttpStatus.OK
                )
        );
    }
    
    
}
