package com.org.bgv.onboarding.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.onboarding.dto.CreateCompanyRequest;
import com.org.bgv.onboarding.service.CompanyRegistrationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/companies")
@RequiredArgsConstructor
@Slf4j
public class CompanyRegistrationController {

    private final CompanyRegistrationService companyRegistrationService;

    /**
     * Step 1
     * Create company in DRAFT state
     */
    @PostMapping
    public ResponseEntity<CustomApiResponse<Long>> createCompany(
            @RequestBody CreateCompanyRequest request) {

        try {

            log.info("CompanyRegistrationController :: createCompany :: {}", request);

            Long companyId = companyRegistrationService.createCompany(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success(
                            "Company created successfully",
                            companyId,
                            HttpStatus.CREATED));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST));

        } catch (Exception e) {

            log.error("Error creating company", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to create company : " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/{companyId}/submit")
    public ResponseEntity<CustomApiResponse<Boolean>> submitRegistration(
            @PathVariable Long companyId) {

        try {

            log.info("submitRegistration :: companyId={}", companyId);

            companyRegistrationService.submitRegistration(companyId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Registration submitted successfully",
                            true,
                            HttpStatus.OK));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to submit registration",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @PostMapping("/{companyId}/approve")
    public ResponseEntity<CustomApiResponse<Boolean>> approveCompany(
            @PathVariable Long companyId) {

        try {

            log.info("approveCompany :: companyId={}", companyId);

            companyRegistrationService.approveCompany(companyId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Company approved successfully",
                            true,
                            HttpStatus.OK));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to approve company",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}