package com.org.bgv.onboarding.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.onboarding.dto.CompanyAddressRequest;
import com.org.bgv.onboarding.dto.CompanyAddressResponse;
import com.org.bgv.onboarding.service.CompanyAddressService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/companies/{companyId}/addresses")
@RequiredArgsConstructor
@Slf4j
public class CompanyAddressController {

    private final CompanyAddressService companyAddressService;

    @PostMapping
    public ResponseEntity<CustomApiResponse<CompanyAddressResponse>> addAddress(
            @PathVariable Long companyId,
            @RequestBody CompanyAddressRequest request) {

        try {

            log.info("addAddress :: companyId={} request={}",
                    companyId, request);

            CompanyAddressResponse companyAddressResponse = companyAddressService.addAddress(companyId, request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success(
                            "Address added successfully",
                            companyAddressResponse,
                            HttpStatus.CREATED));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to add address",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
