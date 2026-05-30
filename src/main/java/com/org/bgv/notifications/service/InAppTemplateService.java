package com.org.bgv.notifications.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.notifications.InAppTemplate;
import com.org.bgv.notifications.NotificationPriority;
import com.org.bgv.notifications.PolicySource;
import com.org.bgv.notifications.dto.InAppTemplateDTO;
import com.org.bgv.notifications.dto.InAppTemplatePreviewDTO;
import com.org.bgv.notifications.repository.InAppTemplateRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InAppTemplateService {

    private final InAppTemplateRepository repository;

   
    @Transactional(readOnly = true)
    public List<InAppTemplateDTO> list() {

        List<InAppTemplate> templates = repository.findByActive(true);

        return templates.stream()
                .map(this::toDto)
                .toList();
    }
    
    
    public InAppTemplateDTO createTemplate(InAppTemplateDTO dto) throws Exception {

        // 🔹 Validate template code
        if (dto.getTemplateCode() == null || dto.getTemplateCode().isBlank()) {
            throw new RuntimeException("Template code is required");
        }

        // 🔹 Validate title
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new RuntimeException("Title is required");
        }

        // 🔹 Validate message
        if (dto.getMessage() == null || dto.getMessage().isBlank()) {
            throw new RuntimeException("Message is required");
        }

        // 🔹 Check duplicate template code for same company
        boolean exists = repository
                .existsByTemplateCode(
                        dto.getTemplateCode()
                        
                );

        if (exists) {
            throw new RuntimeException(
                    "In-App template already exists with code: "
                            + dto.getTemplateCode()
            );
        }

        // 🔹 Create entity
        InAppTemplate template = new InAppTemplate();

        template.setTemplateCode(dto.getTemplateCode());
        template.setTemplate_name(dto.getName());
        template.setCompanyId(dto.getCompanyId());
        template.setTemplate_name(dto.getTitle());
        template.setTitle(dto.getTitle());
        template.setMessage(dto.getMessage());

        template.setDeepLink(dto.getDeepLink());

        template.setType(dto.getType());

        template.setPriority(
                dto.getPriority() != null
                        ? dto.getPriority()
                        : NotificationPriority.MEDIUM
        );

        template.setSource(
                dto.getSource() != null
                        ? dto.getSource()
                        : PolicySource.PLATFORM_DEFAULT
        );

        template.setActive(dto.isActive());

        // 🔹 Save
        InAppTemplate saved = repository.save(template);

        return convertToDTO(saved);
    }
    
    
    
    

    
    @Transactional(readOnly = true)
    public InAppTemplateDTO get(String templateCode, Long companyId) {

        InAppTemplate template =
                repository.findResolvedTemplate(templateCode, companyId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "In-app template not found: " + templateCode
                                )
                        );

        return toDto(template);
    }

    /*
    public InAppTemplateDTO save(
            String templateCode,
            Long companyId,
            InAppTemplateDTO dto
    ) {
        InAppTemplate template =
                repository
                        .findByTemplateCodeAndCompanyId(templateCode, companyId)
                        .orElseGet(() -> createNew(templateCode, companyId));

        template.setTitle(dto.getTitle());
        template.setMessage(dto.getMessage());   // ✅ fixed
        template.setDeepLink(dto.getDeepLink());
        template.setActive(dto.isActive());
        template.setSource(
                companyId == null
                        ? PolicySource.PLATFORM_DEFAULT
                        : PolicySource.COMPANY_OVERRIDE
        );

        return toDto(repository.save(template));
    }
*/
   
    @Transactional(readOnly = true)
    public InAppTemplatePreviewDTO preview(
            String templateCode,
            Map<String, Object> variables
    ) {
        InAppTemplate template =
                repository.findResolvedTemplate(templateCode, null)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "In-app template not found: " + templateCode
                                )
                        );

        InAppTemplatePreviewDTO preview =
                new InAppTemplatePreviewDTO();

        preview.setTitle(
                TemplateEngine.render(template.getTitle(), variables)
        );
        preview.setMessage(
                TemplateEngine.render(template.getMessage(), variables)
        );
        preview.setDeepLink(template.getDeepLink());

        return preview;
    }

    // ---------------- helpers ----------------

    private InAppTemplate createNew(String templateCode, Long companyId) {
        InAppTemplate t = new InAppTemplate();
        t.setTemplateCode(templateCode);
        t.setCompanyId(companyId);
        t.setActive(true);
        return t;
    }

    private InAppTemplateDTO toDto(InAppTemplate entity) {

        InAppTemplateDTO dto = new InAppTemplateDTO();
        dto.setId(entity.getId());
        dto.setTemplateCode(entity.getTemplateCode());
        dto.setCompanyId(entity.getCompanyId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());   // ✅ aligned
        dto.setDeepLink(entity.getDeepLink());
        dto.setSource(entity.getSource());
        dto.setActive(entity.isActive());

        return dto;
    }
    
    private InAppTemplateDTO convertToDTO(InAppTemplate template) {

        InAppTemplateDTO dto = new InAppTemplateDTO();

        dto.setId(template.getId());

        dto.setTemplateCode(template.getTemplateCode());

       // dto.setTemplateName(template.getTemplate_name());

        dto.setCompanyId(template.getCompanyId());

        dto.setTitle(template.getTitle());

        dto.setMessage(template.getMessage());

        dto.setDeepLink(template.getDeepLink());

      //  dto.setType(template.getType());

      //  dto.setPriority(template.getPriority());

        dto.setSource(template.getSource());

        dto.setActive(template.isActive());

        return dto;
    }
}
