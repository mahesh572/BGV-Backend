package com.org.bgv.notifications.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.notifications.dto.EmailSettingsRequest;
import com.org.bgv.notifications.dto.EmailSettingsResponse;
import com.org.bgv.notifications.service.CompanyEmailSettingsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/company/{companyId}/email-settings")
@RequiredArgsConstructor
@Slf4j
public class CompanyEmailSettingsController {

    private final CompanyEmailSettingsService service;

    @GetMapping
    public ResponseEntity<CustomApiResponse<EmailSettingsResponse>> get(
            @PathVariable Long companyId) {

        try {
            EmailSettingsResponse response = service.get(companyId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Company email settings fetched successfully",
                            response,
                            HttpStatus.OK
                    )
            );

        } catch (Exception e) {
            log.error("Failed to fetch company email settings for companyId={}", companyId, e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to fetch company email settings",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @PostMapping
    public ResponseEntity<CustomApiResponse<EmailSettingsResponse>> save(
            @PathVariable Long companyId,
            @RequestBody EmailSettingsRequest request) {

        try {
            EmailSettingsResponse response = service.saveOrUpdate(companyId, request);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Company email settings saved successfully",
                            response,
                            HttpStatus.OK
                    )
            );

        } catch (Exception e) {
            log.error("Failed to save company email settings for companyId={}", companyId, e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to save company email settings",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @PutMapping("/activate")
    public ResponseEntity<CustomApiResponse<Void>> activate(
            @PathVariable Long companyId) {

        try {
            service.activate(companyId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Company email settings activated successfully",
                            null,
                            HttpStatus.OK
                    )
            );

        } catch (Exception e) {
            log.error("Failed to activate company email settings for companyId={}", companyId, e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to activate company email settings",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @PutMapping("/deactivate")
    public ResponseEntity<CustomApiResponse<Void>> deactivate(
            @PathVariable Long companyId) {

        try {
            service.deactivate(companyId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Company email settings deactivated successfully",
                            null,
                            HttpStatus.OK
                    )
            );

        } catch (Exception e) {
            log.error("Failed to deactivate company email settings for companyId={}", companyId, e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to deactivate company email settings",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
}
