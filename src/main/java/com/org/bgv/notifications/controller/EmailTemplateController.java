package com.org.bgv.notifications.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.notifications.dto.EmailTemplateDTO;
import com.org.bgv.notifications.dto.PlaceholderDTO;
import com.org.bgv.notifications.dto.TemplateUserRole;
import com.org.bgv.notifications.service.EmailTemplateService;
import com.org.bgv.notifications.service.PlaceholderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/templates/email")
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateController {

    private final EmailTemplateService service;
    private final PlaceholderService placeholderService;

    @GetMapping
    public ResponseEntity<CustomApiResponse<List<EmailTemplateDTO>>> list(
            @RequestParam(required = false) Long companyId
    ) {
        try {
            List<EmailTemplateDTO> templates = service.list(companyId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Email templates fetched successfully",
                            templates,
                            HttpStatus.OK
                    )
            );

        } catch (Exception e) {
            log.error("Failed to fetch email templates", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to fetch email templates",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @GetMapping("/{templateCode}")
    public ResponseEntity<CustomApiResponse<EmailTemplateDTO>> get(
            @PathVariable String templateCode,
            @RequestParam(required = false) Long companyId
    ) {
        try {
            EmailTemplateDTO template = service.get(templateCode, companyId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Email template fetched successfully",
                            template,
                            HttpStatus.OK
                    )
            );

        } catch (RuntimeException e) {
            log.error("Email template not found: {}", templateCode, e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ));

        } catch (Exception e) {
            log.error("Failed to fetch email template", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to fetch email template",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @PutMapping("/{templateCode}")
    public ResponseEntity<CustomApiResponse<EmailTemplateDTO>> save(
            @PathVariable String templateCode,
            @RequestParam(required = false) Long companyId,
            @RequestBody EmailTemplateDTO dto
    ) {
        log.info("Saving email template templateCode={}, companyId={}", templateCode, companyId);

        try {
            EmailTemplateDTO saved = service.save(templateCode, companyId, dto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success(
                            "Email template saved successfully",
                            saved,
                            HttpStatus.CREATED
                    ));

        } catch (RuntimeException e) {
            log.error("Failed to save email template", e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ));

        } catch (Exception e) {
            log.error("Unexpected error while saving email template", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to save email template",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @PostMapping("/{templateCode}/preview")
    public ResponseEntity<CustomApiResponse<String>> preview(
            @PathVariable String templateCode,
            @RequestBody Map<String, Object> variables
    ) {
        try {
            String preview = service.preview(templateCode, variables);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Email template preview generated successfully",
                            preview,
                            HttpStatus.OK
                    )
            );

        } catch (RuntimeException e) {
            log.error("Failed to generate preview for template {}", templateCode, e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ));

        } catch (Exception e) {
            log.error("Unexpected error while generating email preview", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to generate email preview",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    
    @GetMapping("/placeholders")
    public ResponseEntity<CustomApiResponse<List<PlaceholderDTO>>> getPlaceholders() {

        TemplateUserRole role = null;

        try {
            if (SecurityUtils.hasRole(TemplateUserRole.ADMINISTRATOR.getValue())) {
                role = TemplateUserRole.ADMINISTRATOR;

            } else if (SecurityUtils.hasRole(TemplateUserRole.COMPANY_ADMINISTRATOR.getValue())) {
                role = TemplateUserRole.COMPANY_ADMINISTRATOR;

            } else {
                throw new AccessDeniedException("User role not authorized to access placeholders");
            }

            List<PlaceholderDTO> placeholders =
                    placeholderService.getAllowedPlaceholders(role);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Placeholders fetched successfully",
                            placeholders,
                            HttpStatus.OK
                    )
            );

        } catch (AccessDeniedException e) {
            log.warn("Unauthorized placeholder access attempt");

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.FORBIDDEN
                    ));

        } catch (RuntimeException e) {
            log.error("Failed to fetch placeholders for role {}", role, e);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ));

        } catch (Exception e) {
            log.error("Unexpected error while fetching placeholders", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to fetch placeholders",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }



}


