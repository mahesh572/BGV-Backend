package com.org.bgv.bgvpackage.controller;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.bgvpackage.dto.PlatformCheckPricingRequest;
import com.org.bgv.bgvpackage.dto.PlatformCheckPricingResponse;
import com.org.bgv.bgvpackage.entity.PlatformCheckPricing;
import com.org.bgv.bgvpackage.service.PlatformCheckPricingService;
import com.org.bgv.dto.document.CompanyDto;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.PricingType;
import com.org.bgv.service.CompanyService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/platform-check-pricing")
@RequiredArgsConstructor
public class PlatformCheckPricingController {

    private final PlatformCheckPricingService pricingService;
    private final CompanyService companyService;

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public ResponseEntity<CustomApiResponse<?>> createPricing(
            @RequestBody PlatformCheckPricingRequest platformCheckPricingRequest
            
    ) {

        
                pricingService.createPricing(platformCheckPricingRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomApiResponse.success("Pricing created successfully",
                        null, HttpStatus.CREATED));
    }

    // =========================
    // 	
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse<?>> updatePricing(
            @PathVariable Long id,
            @RequestBody PlatformCheckPricingRequest platformCheckPricingRequest
            
    ) {

    	
                pricingService.updatePricing(id,platformCheckPricingRequest);

        return ResponseEntity.ok(
                CustomApiResponse.success("Pricing updated successfully",
                        "", HttpStatus.OK));
    }

    // =========================
    // DEACTIVATE (Soft Delete)
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse<Void>> deactivatePricing(
            @PathVariable Long id
    ) {

        pricingService.deactivatePricing(id);

        return ResponseEntity.ok(
                CustomApiResponse.success("Pricing deactivated successfully",
                        null, HttpStatus.OK));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<CustomApiResponse<List<PlatformCheckPricingResponse>>> 
    getPricingByCategory(@PathVariable Long categoryId) {

        List<PlatformCheckPricingResponse> pricingList =
                pricingService.getPricingByCategoryId(categoryId);

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Pricing fetched successfully",
                        pricingList,
                        HttpStatus.OK
                )
        );
    }

    @GetMapping("/companies")
    public ResponseEntity<CustomApiResponse<List<CompanyDto>>> 
    getAllCompanies() {

        List<CompanyDto> companies = companyService.getAllCompanies();

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Companies fetched successfully",
                        companies,
                        HttpStatus.OK
                )
        );
    }
    
    
    
}
