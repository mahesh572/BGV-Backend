package com.org.bgv.onboarding.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.onboarding.dto.CompanySpecificationRequest;
import com.org.bgv.onboarding.dto.CompanySpecificationResponse;
import com.org.bgv.onboarding.service.CompanySpecificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/companies/{companyId}/specifications")
@RequiredArgsConstructor
@Slf4j
public class CompanySpecificationController {

    private final CompanySpecificationService companySpecificationService;

    @GetMapping
    public ResponseEntity<CustomApiResponse<List<CompanySpecificationResponse>>>
    getSpecifications(
            @PathVariable Long companyId) {

        try {

            List<CompanySpecificationResponse> response =
                    companySpecificationService.getSpecifications(companyId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Specifications fetched successfully",
                            response,
                            HttpStatus.OK));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to fetch specifications",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @PostMapping
    public ResponseEntity<CustomApiResponse<Void>> saveOrUpdateSpecifications(
            @PathVariable Long companyId,
            @RequestBody List<CompanySpecificationRequest> request) {

        try {

            log.info(
                    "saveOrUpdateSpecifications companyId={} request={}",
                    companyId,
                    request);

            companySpecificationService
                    .saveOrUpdateSpecifications(
                            companyId,
                            request);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Specifications saved successfully",
                            null,
                            HttpStatus.OK));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to save specifications",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}