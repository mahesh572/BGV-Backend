package com.org.bgv.notifications.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

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
import com.org.bgv.common.EmailTemplateDTO;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.notifications.dto.InAppTemplateDTO;
import com.org.bgv.notifications.dto.PlaceholderDTO;
import com.org.bgv.notifications.dto.TemplateUserRole;
import com.org.bgv.notifications.service.InAppTemplateService;
import com.org.bgv.notifications.service.PlaceholderService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/templates/in-app")
@RequiredArgsConstructor
@Slf4j
public class InAppTemplateController {

    private final InAppTemplateService service;
    private final PlaceholderService placeholderService;

    @GetMapping
    public ResponseEntity<CustomApiResponse<List<InAppTemplateDTO>>> list(
            @RequestParam(required = false) Long companyId
    ) {
        try {

            log.info("Fetching in-app templates for companyId: {}", companyId);

            List<InAppTemplateDTO> templates = service.list();

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "In-app templates fetched successfully",
                            templates,
                            HttpStatus.OK
                    )
            );

        } catch (RuntimeException e) {

            log.error("Error fetching in-app templates: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            CustomApiResponse.failure(
                                    e.getMessage(),
                                    HttpStatus.BAD_REQUEST
                            )
                    );

        } catch (Exception e) {

            log.error("Unexpected error fetching in-app templates: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            CustomApiResponse.failure(
                                    "Failed to fetch in-app templates: " + e.getMessage(),
                                    HttpStatus.INTERNAL_SERVER_ERROR
                            )
                    );
        }
    }
/*
    @PutMapping("/{templateCode}")
    public InAppTemplateDTO save(
            @PathVariable String templateCode,
            @RequestParam(required = false) Long copanyId,
            @RequestBody InAppTemplateDTO dto
    ) {
        return service.save(templateCode, copanyId, dto);
    }
    */
    @PostMapping
    public ResponseEntity<CustomApiResponse<InAppTemplateDTO>> createTemplate(@Valid @RequestBody InAppTemplateDTO templateDTO) {
        try {
            log.info("email-templates/create::::::{}", templateDTO);
            InAppTemplateDTO createdTemplate = service.createTemplate(templateDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success("Template created successfully", createdTemplate, HttpStatus.CREATED));
        } catch (EntityNotFoundException e) {
        	log.warn("Template already exists with type: {}", templateDTO.getType());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.CONFLICT));
        } catch (RuntimeException e) {
        	log.error("Error creating template: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
        	log.error("Unexpected error creating template: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to create template: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
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
                    placeholderService.getInAppAllowedPlaceholders(role);

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

