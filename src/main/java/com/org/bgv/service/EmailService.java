package com.org.bgv.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.org.bgv.common.EmailTemplateDTO;
import com.org.bgv.entity.Email;
import com.org.bgv.entity.EmailTemplate;
import com.org.bgv.repository.EmailTemplateRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);
	
	private final EmailTemplateRepository emailTemplateRepository;
	
	private final FileReaderService fileReaderService;
	
	private final Map<String, TemplateConfig> templateConfigs = Map.of(
	        "account_creation", new TemplateConfig("Welcome New User", "templates/email/account-creation.html", "templates/email/account-creation.txt"),
	        "password_reset", new TemplateConfig("Reset Your Password", "templates/email/password-reset.html", "templates/email/password-reset.txt"),
	        "password_change", new TemplateConfig("Password Changed Confirmation", "templates/email/password-change.html", "templates/email/password-change.txt"),
	        "email_verification", new TemplateConfig("Email Verification", "templates/email/email-verification.html", "templates/email/email-verification.txt"),
	        "two_factor_auth", new TemplateConfig("2FA Verification Code", "templates/email/two-factor-auth.html", "templates/email/two-factor-auth.txt")
	    );
	
	/**
     * Initialize templates from files
     */
    public void initializeTemplatesFromFiles() {
    	log.info("Initializing email templates from files...");
        
        for (Map.Entry<String, TemplateConfig> entry : templateConfigs.entrySet()) {
            String type = entry.getKey();
            TemplateConfig config = entry.getValue();
            
            try {
                // Check if template already exists
                if (emailTemplateRepository.existsByType(type)) {
                	log.info("Template {} already exists, skipping initialization", type);
                    continue;
                }
                
                // Read content from files
                String htmlContent = fileReaderService.readFileFromClasspath(config.getHtmlFilePath());
                String textContent = fileReaderService.fileExists(config.getTextFilePath()) ? 
                    fileReaderService.readFileFromClasspath(config.getTextFilePath()) : "";
                
                // Create template entity
                EmailTemplate template = new EmailTemplate();
                template.setName(config.getName());
                template.setType(type);
                template.setSubject(getDefaultSubject(type));
                template.setBodyHtml(htmlContent);
                template.setBodyText(textContent);
                template.setIsActive(true);
                
                emailTemplateRepository.save(template);
                log.info("Successfully initialized template: {}", type);
                
            } catch (Exception e) {
            	log.error("Failed to initialize template {}: {}", type, e.getMessage());
            }
        }
        
        log.info("Email template initialization completed");
    }
    /**
     * Update template from file
     */
    public void updateTemplateFromFile(String type) {
        TemplateConfig config = templateConfigs.get(type);
        if (config == null) {
            throw new RuntimeException("Template type not found: " + type);
        }
        
        EmailTemplate template = emailTemplateRepository.findByType(type)
                .orElseThrow(() -> new EntityNotFoundException("Template not found for type: " + type));
        
        try {
            String htmlContent = fileReaderService.readFileFromClasspath(config.getHtmlFilePath());
            String textContent = fileReaderService.fileExists(config.getTextFilePath()) ? 
                fileReaderService.readFileFromClasspath(config.getTextFilePath()) : "";
            
            template.setBodyHtml(htmlContent);
            template.setBodyText(textContent);
            
            emailTemplateRepository.save(template);
            log.info("Successfully updated template from file: {}", type);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to update template from file: " + type, e);
        }
    }

    /**
     * Reload all templates from files
     */
    public void reloadAllTemplatesFromFiles() {
    	log.info("Reloading all templates from files...");
        
        for (String type : templateConfigs.keySet()) {
            if (emailTemplateRepository.existsByType(type)) {
                updateTemplateFromFile(type);
            }
        }
    }

    private String getDefaultSubject(String type) {
        switch (type) {
            case "account_creation":
                return "Welcome to {company}, {name}!";
            case "password_reset":
                return "Password Reset Request - {company}";
            case "password_change":
                return "Your Password Has Been Changed";
            case "email_verification":
                return "Verify Your Email Address - {company}";
            case "two_factor_auth":
                return "Your Verification Code - {company}";
            default:
                return "Email from {company}";
        }
    }
	/**
     * Get all active email templates
     */
    public List<EmailTemplateDTO> getAllActiveTemplates() {
        return emailTemplateRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
   
    /**
     * Get template by type (for dropdown selection)
     */
    public EmailTemplateDTO getTemplateByType(String type) {
        EmailTemplate template = emailTemplateRepository.findByType(type)
                .orElseThrow(() -> new EntityNotFoundException("Template not found for type: " + type));
        return convertToDTO(template);
    }
    /**
     * Get template by ID
     * @throws Exception 
     */
    public EmailTemplateDTO getTemplateById(Long id) throws Exception {
        EmailTemplate template = null;
		try {
			template = emailTemplateRepository.findById(id)
			        .orElseThrow(() -> new Exception("Template not found for id: " + id));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return convertToDTO(template);
    }
    /**
     * Get simplified template list for dropdown
     */
    public List<Map<String, String>> getTemplateDropdown() {
        return emailTemplateRepository.findByIsActiveTrue()
                .stream()
                .map(template -> Map.of(
                    "value", template.getType(),
                    "label", template.getName(),
                    "id", template.getId().toString()
                ))
                .collect(Collectors.toList());
    }
    /**
     * Create a new email template
     * @throws Exception 
     */
    public EmailTemplateDTO createTemplate(EmailTemplateDTO templateDTO) throws Exception {
        // Check if template type already exists
        if (emailTemplateRepository.existsByType(templateDTO.getType())) {
            throw new Exception("Template with type '" + templateDTO.getType() + "' already exists");
        }

        EmailTemplate template = new EmailTemplate();
        template.setName(templateDTO.getName());
        template.setType(templateDTO.getType());
        template.setSubject(templateDTO.getSubject());
        template.setBodyHtml(templateDTO.getBodyHtml());
        template.setBodyText(templateDTO.getBodyText());
        template.setIsActive(templateDTO.getIsActive() != null ? templateDTO.getIsActive() : true);

        EmailTemplate savedTemplate = emailTemplateRepository.save(template);
        return convertToDTO(savedTemplate);
    }
    /**
     * Update an existing email template
     * @throws Exception 
     */
    public EmailTemplateDTO updateTemplate(Long id, EmailTemplateDTO templateDTO) throws Exception {
        EmailTemplate existingTemplate = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found for id: " + id));

        // Check if type is being changed and if new type already exists
        if (!existingTemplate.getType().equals(templateDTO.getType()) && 
            emailTemplateRepository.existsByType(templateDTO.getType())) {
            throw new Exception("Template with type '" + templateDTO.getType() + "' already exists");
        }

        existingTemplate.setName(templateDTO.getName());
        existingTemplate.setType(templateDTO.getType());
        existingTemplate.setSubject(templateDTO.getSubject());
        existingTemplate.setBodyHtml(templateDTO.getBodyHtml());
        existingTemplate.setBodyText(templateDTO.getBodyText());
        if (templateDTO.getIsActive() != null) {
            existingTemplate.setIsActive(templateDTO.getIsActive());
        }

        EmailTemplate updatedTemplate = emailTemplateRepository.save(existingTemplate);
        return convertToDTO(updatedTemplate);
    }
    
    /**
     * Delete a template (soft delete by setting inactive)
     */
    public void deleteTemplate(Long id) {
        EmailTemplate template = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found for id: " + id));
        template.setIsActive(false);
        emailTemplateRepository.save(template);
    }
    /**
     * Get template with variables replaced (for preview)
     */
    public EmailTemplateDTO getTemplateWithVariables(String type, Map<String, String> variables) {
        EmailTemplateDTO template = getTemplateByType(type);
        
        // Replace variables in subject and body
        String processedSubject = replaceVariables(template.getSubject(), variables);
        String processedBodyHtml = replaceVariables(template.getBodyHtml(), variables);
        String processedBodyText = replaceVariables(template.getBodyText(), variables);
        
        template.setSubject(processedSubject);
        template.setBodyHtml(processedBodyHtml);
        template.setBodyText(processedBodyText);
        
        return template;
    }
    
    /**
     * Helper method to replace template variables
     */
    private String replaceVariables(String text, Map<String, String> variables) {
        if (text == null) return null;
        
        String result = text;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            result = result.replace(placeholder, entry.getValue() != null ? entry.getValue() : "");
        }
        return result;
    }
    
    /**
     * Convert Entity to DTO
     */
    private EmailTemplateDTO convertToDTO(EmailTemplate template) {
        EmailTemplateDTO dto = new EmailTemplateDTO();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setType(template.getType());
        dto.setSubject(template.getSubject());
        dto.setBodyHtml(template.getBodyHtml());
        dto.setBodyText(template.getBodyText());
        dto.setIsActive(template.getIsActive());
        dto.setCreatedAt(template.getCreatedAt());
        return dto;
    }
    
    
    private static class TemplateConfig {
        private final String name;
        private final String htmlFilePath;
        private final String textFilePath;

        public TemplateConfig(String name, String htmlFilePath, String textFilePath) {
            this.name = name;
            this.htmlFilePath = htmlFilePath;
            this.textFilePath = textFilePath;
        }

        public String getName() { return name; }
        public String getHtmlFilePath() { return htmlFilePath; }
        public String getTextFilePath() { return textFilePath; }
    }
}
