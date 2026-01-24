package com.org.bgv.notifications.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.dto.NotificationPolicyDTO;
import com.org.bgv.notifications.service.NotificationPolicyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/notification-policies")
@RequiredArgsConstructor
@Slf4j
public class NotificationPolicyController {

    private final NotificationPolicyService policyService;

    // 1️⃣ Get all policies (platform or employer)
    @GetMapping
    public ResponseEntity<CustomApiResponse<List<NotificationPolicyDTO>>> getPolicies(
            @RequestParam(required = false) Long companyId
    ) {
        log.info("Fetching notification policies, companyId={}", companyId);

        try {
            List<NotificationPolicyDTO> policies =
                    policyService.getPolicies(companyId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Notification policies fetched successfully",
                            policies,
                            HttpStatus.OK
                    )
            );

        } catch (RuntimeException e) {
            log.error("Failed to fetch notification policies", e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ));

        } catch (Exception e) {
            log.error("Unexpected error while fetching notification policies", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to fetch notification policies",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // 2️⃣ Get single policy by event
    @GetMapping("/{event}")
    public ResponseEntity<CustomApiResponse<NotificationPolicyDTO>> getPolicy(
            @PathVariable NotificationEvent event,
            @RequestParam(required = false) Long companyId
    ) {
        log.info("Fetching notification policy for event={}, companyId={}", event, companyId);

        try {
            NotificationPolicyDTO policy =
                    policyService.getPolicy(event, companyId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Notification policy fetched successfully",
                            policy,
                            HttpStatus.OK
                    )
            );

        } catch (RuntimeException e) {
            log.error("Failed to fetch notification policy", e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ));

        } catch (Exception e) {
            log.error("Unexpected error while fetching notification policy", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to fetch notification policy",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // 3️⃣ Save / Update policy (platform or employer override)
    @PutMapping("/{event}")
    public ResponseEntity<CustomApiResponse<NotificationPolicyDTO>> savePolicy(
            @PathVariable NotificationEvent event,
            @RequestParam(required = false) Long companyId,
            @RequestBody NotificationPolicyDTO dto
    ) {
        log.info("Saving notification policy for event={}, companyId={}", event, companyId);

        try {
            NotificationPolicyDTO saved =
                    policyService.savePolicy(event, companyId, dto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success(
                            "Notification policy saved successfully",
                            saved,
                            HttpStatus.CREATED
                    ));

        } catch (RuntimeException e) {
            log.error("Failed to save notification policy", e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ));

        } catch (Exception e) {
            log.error("Unexpected error while saving notification policy", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to save notification policy",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // 4️⃣ Reset employer override to platform default
    @DeleteMapping("/{event}/override")
    public ResponseEntity<CustomApiResponse<Void>> resetOverride(
            @PathVariable NotificationEvent event,
            @RequestParam Long companyId
    ) {
        log.info("Resetting notification policy override for event={}, companyId={}", event, companyId);

        try {
            policyService.resetToPlatformDefault(event, companyId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Notification policy reset to platform default",
                            null,
                            HttpStatus.OK
                    )
            );

        } catch (RuntimeException e) {
            log.error("Failed to reset notification policy override", e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ));

        } catch (Exception e) {
            log.error("Unexpected error while resetting notification policy", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to reset notification policy override",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
}

