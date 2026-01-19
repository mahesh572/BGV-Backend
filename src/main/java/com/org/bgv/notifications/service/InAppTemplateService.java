package com.org.bgv.notifications.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.notifications.InAppTemplate;
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
    public List<InAppTemplateDTO> list(Long companyId) {

        List<InAppTemplate> templates =
                companyId == null
                        ? repository.findByCompanyIdIsNull()
                        : repository.findByCompanyId(companyId);

        return templates.stream()
                .map(this::toDto)
                .toList();
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
}
