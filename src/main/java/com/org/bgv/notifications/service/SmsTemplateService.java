package com.org.bgv.notifications.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.notifications.PolicySource;
import com.org.bgv.notifications.dto.SmsTemplateDTO;
import com.org.bgv.notifications.entity.SmsTemplate;
import com.org.bgv.notifications.repository.SmsTemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SmsTemplateService {

    private final SmsTemplateRepository repository;

    
    @Transactional(readOnly = true)
    public List<SmsTemplateDTO> list(Long companyId) {

        List<SmsTemplate> templates =
        		companyId == null
                        ? repository.findByCompanyIdIsNull()
                        : repository.findByCompanyId(companyId);

        return templates.stream()
                .map(this::toDto)
                .toList();
    }

    
    @Transactional(readOnly = true)
    public SmsTemplateDTO get(String templateCode, Long companyId) {

        SmsTemplate template =
                repository.findResolvedTemplate(templateCode, companyId);
                        

        return toDto(template);
    }

   
    public SmsTemplateDTO save(
            String templateCode,
            Long companyId,
            SmsTemplateDTO dto
    ) {
        SmsTemplate template =
                repository.findByTemplateCodeAndCompanyId(
                        templateCode,
                        companyId
                ).orElseGet(() -> createNew(templateCode, companyId));

        template.setMessage(dto.getMessage());
        template.setActive(dto.isActive());
        template.setSource(
        		companyId == null
                        ? PolicySource.PLATFORM_DEFAULT
                        : PolicySource.COMPANY_OVERRIDE
        );

        return toDto(repository.save(template));
    }

   
    @Transactional(readOnly = true)
    public String preview(String templateCode,
                          Map<String, Object> variables) {

        SmsTemplate template =
                repository.findResolvedTemplate(templateCode, null);
                        

        return TemplateEngine.render(
                template.getMessage(),
                variables
        );
    }

    // -------------------------
    // Helpers
    // -------------------------

    private SmsTemplate createNew(String templateCode, Long companyId) {

        SmsTemplate template = new SmsTemplate();
        template.setTemplateCode(templateCode);
        template.setCompanyId(companyId);
        template.setActive(true);

        return template;
    }

    private SmsTemplateDTO toDto(SmsTemplate entity) {

        SmsTemplateDTO dto = new SmsTemplateDTO();
        dto.setId(entity.getId());
        dto.setTemplateCode(entity.getTemplateCode());
        dto.setCompanyId(entity.getCompanyId());
        dto.setMessage(entity.getMessage());
        dto.setActive(entity.isActive());
        dto.setSource(entity.getSource());

        return dto;
    }
}
