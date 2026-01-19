package com.org.bgv.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.EmailTemplateDTO;
import com.org.bgv.service.EmailService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/email-templates")
@CrossOrigin(origins = "*")
public class EmailTemplatefirstController {

    private static final Logger logger = LoggerFactory.getLogger(EmailTemplatefirstController.class);
    
    private final EmailService emailTemplateService;

    public EmailTemplatefirstController(EmailService emailTemplateService) {
        this.emailTemplateService = emailTemplateService;
    }

    /**
     * Get all active templates
     */
    @GetMapping
    public ResponseEntity<CustomApiResponse<List<EmailTemplateDTO>>> getAllActiveTemplates() {
        try {
            logger.info("email-templates/getAllActiveTemplates");
            List<EmailTemplateDTO> templates = emailTemplateService.getAllActiveTemplates();
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Templates retrieved successfully", templates, HttpStatus.OK));
        } catch (RuntimeException e) {
            logger.error("Error retrieving templates: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving templates: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve templates: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * Get template by type - used when dropdown selection changes
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<CustomApiResponse<EmailTemplateDTO>> getTemplateByType(@PathVariable String type) {
        try {
            logger.info("email-templates/getTemplateByType::::::{}", type);
            EmailTemplateDTO template = emailTemplateService.getTemplateByType(type);
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Template retrieved successfully", template, HttpStatus.OK));
        } catch (EntityNotFoundException e) {
            logger.warn("Template not found for type: {}", type);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (RuntimeException e) {
            logger.error("Error retrieving template by type {}: {}", type, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving template by type {}: {}", type, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve template: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * Get template by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<EmailTemplateDTO>> getTemplateById(@PathVariable Long id) {
        try {
            logger.info("email-templates/getTemplateById::::::{}", id);
            EmailTemplateDTO template = emailTemplateService.getTemplateById(id);
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Template retrieved successfully", template, HttpStatus.OK));
        } catch (EntityNotFoundException e) {
            logger.warn("Template not found for id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (RuntimeException e) {
            logger.error("Error retrieving template by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving template by id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve template: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * Get simplified template list for dropdown
     */
    @GetMapping("/dropdown")
    public ResponseEntity<CustomApiResponse<List<Map<String, String>>>> getTemplateDropdown() {
        try {
            logger.info("email-templates/getTemplateDropdown");
            List<Map<String, String>> dropdownOptions = emailTemplateService.getTemplateDropdown();
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Dropdown options retrieved successfully", dropdownOptions, HttpStatus.OK));
        } catch (RuntimeException e) {
            logger.error("Error retrieving template dropdown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving template dropdown: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve dropdown options: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * Create a new template
     */
    @PostMapping
    public ResponseEntity<CustomApiResponse<EmailTemplateDTO>> createTemplate(@Valid @RequestBody EmailTemplateDTO templateDTO) {
        try {
            logger.info("email-templates/create::::::{}", templateDTO);
            EmailTemplateDTO createdTemplate = emailTemplateService.createTemplate(templateDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success("Template created successfully", createdTemplate, HttpStatus.CREATED));
        } catch (EntityNotFoundException e) {
            logger.warn("Template already exists with type: {}", templateDTO.getType());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.CONFLICT));
        } catch (RuntimeException e) {
            logger.error("Error creating template: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("Unexpected error creating template: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to create template: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * Update an existing template
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse<EmailTemplateDTO>> updateTemplate(
            @PathVariable Long id, 
            @Valid @RequestBody EmailTemplateDTO templateDTO) {
        try {
            logger.info("email-templates/update::::::id={}, template={}", id, templateDTO);
            EmailTemplateDTO updatedTemplate = emailTemplateService.updateTemplate(id, templateDTO);
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Template updated successfully", updatedTemplate, HttpStatus.OK));
        } catch (EntityNotFoundException e) {
            logger.warn("Template not found for update id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        }  catch (RuntimeException e) {
            logger.error("Error updating template id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("Unexpected error updating template id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update template: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * Delete a template (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse<Void>> deleteTemplate(@PathVariable Long id) {
        try {
            logger.info("email-templates/delete::::::{}", id);
            emailTemplateService.deleteTemplate(id);
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Template deleted successfully", null, HttpStatus.OK));
        } catch (EntityNotFoundException e) {
            logger.warn("Template not found for delete id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (RuntimeException e) {
            logger.error("Error deleting template id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("Unexpected error deleting template id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete template: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * Preview template with variables replaced
     */
    @PostMapping("/{type}/preview")
    public ResponseEntity<CustomApiResponse<EmailTemplateDTO>> previewTemplate(
            @PathVariable String type,
            @RequestBody Map<String, String> variables) {
        try {
            logger.info("email-templates/preview::::::type={}, variables={}", type, variables);
            EmailTemplateDTO preview = emailTemplateService.getTemplateWithVariables(type, variables);
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Template preview generated successfully", preview, HttpStatus.OK));
        } catch (EntityNotFoundException e) {
            logger.warn("Template not found for preview type: {}", type);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (RuntimeException e) {
            logger.error("Error generating preview for type {}: {}", type, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("Unexpected error generating preview for type {}: {}", type, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to generate preview: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * Check if template type exists
     */
    @GetMapping("/exists/{type}")
    public ResponseEntity<CustomApiResponse<Boolean>> checkTemplateExists(@PathVariable String type) {
        try {
            logger.info("email-templates/exists::::::{}", type);
            boolean exists = emailTemplateService.getTemplateDropdown()
                    .stream()
                    .anyMatch(template -> template.get("value").equals(type));
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Existence check completed", exists, HttpStatus.OK));
        } catch (RuntimeException e) {
            logger.error("Error checking template existence for type {}: {}", type, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("Unexpected error checking template existence for type {}: {}", type, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to check template existence: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    /**
     * Reload specific template from file
     */
    @PutMapping("/reload/{type}")
    public ResponseEntity<CustomApiResponse<Void>> reloadTemplateFromFile(@PathVariable String type) {
        try {
            logger.info("email-templates/files/reload::::::{}", type);
            emailTemplateService.updateTemplateFromFile(type);
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Template reloaded successfully from file", null, HttpStatus.OK));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            logger.error("Error reloading template {} from file: {}", type, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to reload template: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * Reload all templates from files
     */
    @PutMapping("/reload-all")
    public ResponseEntity<CustomApiResponse<Void>> reloadAllTemplatesFromFiles() {
        try {
            logger.info("email-templates/files/reload-all");
            emailTemplateService.reloadAllTemplatesFromFiles();
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("All templates reloaded successfully from files", null, HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Error reloading all templates from files: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to reload templates: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
