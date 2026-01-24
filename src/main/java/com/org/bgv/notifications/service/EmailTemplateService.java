package com.org.bgv.notifications.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.notifications.PolicySource;
import com.org.bgv.notifications.dto.EmailTemplateDTO;
import com.org.bgv.notifications.dto.EmailTemplateOptionDTO;
import com.org.bgv.notifications.entity.EmailTemplate;
import com.org.bgv.repository.EmailTemplateRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailTemplateService {

    private final EmailTemplateRepository repository;

    
    /**
     * Admin use-case:
     * Fetch platform email templates (companyId = null)
     */
    public List<EmailTemplateOptionDTO> getPlatformTemplatesForAdmin() {

        return repository
                .findByCompanyIdIsNullAndIsActiveTrue()
                .stream()
                .map(t -> EmailTemplateOptionDTO.builder()
                        .templateCode(t.getTemplateCode())
                        .displayName(t.getName())
                        // .description(t.getDescription()) // if/when available
                        .build()
                )
                .toList();
    }

   
    @Transactional(readOnly = true)
    public List<EmailTemplateDTO> list(Long companyId) {
        List<EmailTemplate> templates =
                companyId == null
                        ? repository.findByCompanyIdIsNull()
                        : repository.findByCompanyId(companyId);

        return templates.stream()
                .map(this::toDto)
                .toList();
    }

    
    @Transactional(readOnly = true)
    public EmailTemplateDTO get(String templateCode, Long companyId) {
        EmailTemplate template =
                repository.findResolvedTemplate(templateCode, companyId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Email template not found: " + templateCode
                                )
                        );

        return toDto(template);
    }

    
    public EmailTemplateDTO save(String templateCode, Long companyId, EmailTemplateDTO dto) {
        EmailTemplate template =
                repository.findByTemplateCodeAndCompanyId(templateCode, companyId)
                        .orElseGet(() -> createNew(templateCode, companyId));

        template.setSubject(dto.getSubject());
        template.setBodyHtml(dto.getBodyHtml());
        template.setIsActive(dto.isActive());
        template.setSource(companyId == null
                ? PolicySource.PLATFORM_DEFAULT
                : PolicySource.COMPANY_OVERRIDE);

        return toDto(repository.save(template));
    }

   
    @Transactional(readOnly = true)
    public String preview(String templateCode, Map<String, Object> variables) {
        EmailTemplate template =
                repository.findResolvedTemplate(templateCode, null).orElseThrow(null);
                        

        return TemplateEngine.render(template.getBodyHtml(), variables);
    }

    // -------------------------
    // Helpers
    // -------------------------

    private EmailTemplate createNew(String templateCode, Long companyId) {
        EmailTemplate template = new EmailTemplate();
        template.setTemplateCode(templateCode);
        template.setCompanyId(companyId);
        template.setIsActive(true);
        return template;
    }

    private EmailTemplateDTO toDto(EmailTemplate entity) {
        EmailTemplateDTO dto = new EmailTemplateDTO();
        dto.setId(entity.getId());
        dto.setTemplateCode(entity.getTemplateCode());
        dto.setCompanyId(entity.getCompanyId());
        dto.setSubject(entity.getSubject());
        dto.setBodyHtml(entity.getBodyHtml());
        dto.setActive(entity.getIsActive());
        dto.setSource(entity.getSource());
        return dto;
    }
}
