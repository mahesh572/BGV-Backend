package com.org.bgv.bgvpackage.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.bgvpackage.dto.EmployerCheckPricingRequest;
import com.org.bgv.bgvpackage.dto.EmployerCheckPricingResponse;
import com.org.bgv.bgvpackage.service.EmployerCheckPricingService;

@RestController
@RequestMapping("/api/admin/employer-check-pricing")
@RequiredArgsConstructor
public class EmployerCheckPricingController {

    private final EmployerCheckPricingService service;

    // ======================================================
    // CREATE OR UPDATE EMPLOYER PRICING
    // ======================================================
    @PostMapping
    public ResponseEntity<CustomApiResponse<EmployerCheckPricingResponse>> createOrUpdate(
            @Valid @RequestBody EmployerCheckPricingRequest request) {

        EmployerCheckPricingResponse response =
                service.createOrUpdate(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomApiResponse.success(
                        "Employer pricing saved successfully",
                        response,
                        HttpStatus.CREATED
                ));
    }

    // ======================================================
    // GET EMPLOYER PRICING BY COMPANY & CATEGORY
    // ======================================================
    @GetMapping
    public ResponseEntity<CustomApiResponse<List<EmployerCheckPricingResponse>>> getByCompanyAndCategory(
            @RequestParam Long companyId,
            @RequestParam Long categoryId) {

        List<EmployerCheckPricingResponse> list =
                service.getByCompanyAndCategory(companyId, categoryId);

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        null,
                        list,
                        HttpStatus.OK
                )
        );
    }

    // ======================================================
    // RESOLVE FINAL PRICE (EMPLOYER → FALLBACK PLATFORM)
    // ======================================================
    @GetMapping("/resolve")
    public ResponseEntity<CustomApiResponse<Double>> resolvePrice(
            @RequestParam Long companyId,
            @RequestParam Long categoryId,
            @RequestParam Long ruleTypeId) {

        Double price =
                service.resolveFinalPrice(companyId, categoryId, ruleTypeId);

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Final price resolved",
                        price,
                        HttpStatus.OK
                )
        );
    }

    // ======================================================
    // SOFT DELETE
    // ======================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse<String>> deactivate(
            @PathVariable Long id) {

        service.deactivate(id);

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Employer pricing deactivated successfully",
                        null,
                        HttpStatus.OK
                )
        );
    }
}
