package com.org.bgv.notifications.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.PolicySource;
import com.org.bgv.notifications.dto.NotificationPolicyChannelDTO;
import com.org.bgv.notifications.dto.NotificationPolicyDTO;
import com.org.bgv.notifications.entity.NotificationPolicy;
import com.org.bgv.notifications.entity.NotificationPolicyChannel;
import com.org.bgv.notifications.repository.NotificationPolicyRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationPolicyService {

    private final NotificationPolicyRepository policyRepository;

    
    @Transactional(readOnly = true)
    public List<NotificationPolicyDTO> getPolicies(Long companyId) {

        List<NotificationPolicy> policies =
        		companyId == null
                        ? policyRepository.findByCompanyIdIsNull()
                        : policyRepository.findByCompanyId(companyId);

        return policies.stream()
                .map(this::toDto)
                .toList();
    }

   
    @Transactional(readOnly = true)
    public NotificationPolicyDTO getPolicy(
            NotificationEvent event,
            Long companyId
    ) {
        NotificationPolicy policy =
                policyRepository
                        .findByEventAndCompanyId(event, companyId)
                        .or(() ->
                                policyRepository
                                        .findByEventAndCompanyId(event, null)
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Notification policy not found for event: " + event
                                )
                        );

        return toDto(policy);
    }


   
    public NotificationPolicyDTO savePolicy(
            NotificationEvent event,
            Long companyId,
            NotificationPolicyDTO dto
    ) {
        NotificationPolicy policy =
                policyRepository
                        .findByEventAndCompanyId(event, companyId)
                        .orElseGet(() -> createNewPolicy(event, companyId));

        policy.setActive(dto.isActive());

        // Replace channels (simple + safe)
        policy.getChannels().clear();

        for (NotificationPolicyChannelDTO chDto : dto.getChannels()) {
            policy.getChannels().add(
                    toEntity(chDto, policy)
            );
        }

        return toDto(policyRepository.save(policy));
    }

   
    public void resetToPlatformDefault(
            NotificationEvent event,
            Long companyId
    ) {
        policyRepository
                .findByEventAndCompanyId(event, companyId)
                .ifPresent(policyRepository::delete);
    }

    // -------------------------
    // Helpers
    // -------------------------

    private NotificationPolicy createNewPolicy(
            NotificationEvent event,
            Long employerId
    ) {
        NotificationPolicy policy = new NotificationPolicy();
        policy.setEvent(event);
        policy.setCompanyId(employerId);
        policy.setActive(true);
        policy.setSource(
                employerId == null
                        ? PolicySource.PLATFORM_DEFAULT
                        : PolicySource.COMPANY_OVERRIDE
        );
        return policy;
    }

    private NotificationPolicyDTO toDto(NotificationPolicy entity) {

        NotificationPolicyDTO dto = new NotificationPolicyDTO();
        dto.setId(entity.getId());
        dto.setEvent(entity.getEvent());
        dto.setCompanyId(entity.getCompanyId());
        dto.setActive(entity.isActive());
        dto.setSource(entity.getSource());

        dto.setChannels(
                entity.getChannels()
                        .stream()
                        .map(this::toDto)
                        .toList()
        );

        return dto;
    }

    private NotificationPolicyChannelDTO toDto(
            NotificationPolicyChannel entity
    ) {
        NotificationPolicyChannelDTO dto =
                new NotificationPolicyChannelDTO();

        dto.setId(entity.getId());
        dto.setChannel(entity.getChannel());
        dto.setEnabled(entity.isEnabled());
        dto.setRecipient(entity.getRecipient());
        dto.setTemplateCode(entity.getTemplateCode());
        dto.setPriority(entity.getPriority());

        return dto;
    }

    private NotificationPolicyChannel toEntity(
            NotificationPolicyChannelDTO dto,
            NotificationPolicy policy
    ) {
        NotificationPolicyChannel entity =
                new NotificationPolicyChannel();

        entity.setPolicy(policy);
        entity.setChannel(dto.getChannel());
        entity.setEnabled(dto.isEnabled());
        entity.setRecipient(dto.getRecipient());
        entity.setTemplateCode(dto.getTemplateCode());
        entity.setPriority(dto.getPriority());

        return entity;
    }
}
