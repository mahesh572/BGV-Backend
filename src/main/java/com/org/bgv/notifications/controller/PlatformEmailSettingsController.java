package com.org.bgv.notifications.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.notifications.dto.EmailSettingsRequest;
import com.org.bgv.notifications.dto.EmailSettingsResponse;
import com.org.bgv.notifications.service.PlatformEmailSettingsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/email-settings")
@RequiredArgsConstructor
@Slf4j
public class PlatformEmailSettingsController {

    private final PlatformEmailSettingsService service;

    @GetMapping
    public ResponseEntity<CustomApiResponse<EmailSettingsResponse>> getActive() {
        try {
            EmailSettingsResponse response = service.getActiveSettings();

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Platform email settings fetched successfully",
                            response,
                            HttpStatus.OK
                    )
            );

        } catch (Exception e) {
            log.error("Failed to fetch platform email settings", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to fetch platform email settings",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @PostMapping
    public ResponseEntity<CustomApiResponse<EmailSettingsResponse>> save(
            @RequestBody EmailSettingsRequest request) {

        try {
            EmailSettingsResponse response = service.saveOrUpdate(request);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Platform email settings saved successfully",
                            response,
                            HttpStatus.OK
                    )
            );

        } catch (Exception e) {
            log.error("Failed to save platform email settings", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to save platform email settings",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<CustomApiResponse<Void>> activate(@PathVariable Long id) {
        try {
            service.activate(id);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Platform email settings activated successfully",
                            null,
                            HttpStatus.OK
                    )
            );

        } catch (Exception e) {
            log.error("Failed to activate platform email settings", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to activate platform email settings",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<CustomApiResponse<Void>> deactivate(@PathVariable Long id) {
        try {
            service.deactivate(id);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Platform email settings deactivated successfully",
                            null,
                            HttpStatus.OK
                    )
            );

        } catch (Exception e) {
            log.error("Failed to deactivate platform email settings", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to deactivate platform email settings",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
}

