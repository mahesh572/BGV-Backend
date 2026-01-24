package com.org.bgv.notifications.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.PolicySource;
import com.org.bgv.notifications.dto.NotificationPolicyChannelDTO;
import com.org.bgv.notifications.dto.NotificationPolicyDTO;
import com.org.bgv.notifications.dto.NotificationRecipientPolicyDTO;
import com.org.bgv.notifications.entity.NotificationPolicy;
import com.org.bgv.notifications.entity.NotificationPolicyChannel;
import com.org.bgv.notifications.entity.NotificationPolicyRecipient;
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


   
    @Transactional
    public NotificationPolicyDTO savePolicy(
            NotificationEvent event,
            Long companyId,
            NotificationPolicyDTO dto
    ) {
        NotificationPolicy policy = findOrCreatePolicy(event, companyId);

        policy.setActive(dto.isActive());
        policy.setSource(
                companyId == null
                    ? PolicySource.PLATFORM_DEFAULT
                    : PolicySource.COMPANY_OVERRIDE
        );

        syncRecipients(policy, dto.getRecipients());

        return toDto(policyRepository.save(policy));
    }


    private NotificationPolicy findOrCreatePolicy(
            NotificationEvent event,
            Long companyId
    ) {
        return policyRepository
                .findByEventAndCompanyId(event, companyId)
                .orElseGet(() -> {

                    if (companyId != null) {
                        NotificationPolicy base =
                                policyRepository
                                        .findByEventAndCompanyId(event, null)
                                        .orElseThrow(() ->
                                                new IllegalStateException(
                                                        "Platform default missing for " + event
                                                )
                                        );
                        return clonePolicy(base, companyId);
                    }

                    NotificationPolicy policy = new NotificationPolicy();
                    policy.setEvent(event);
                    policy.setCompanyId(null);
                    policy.setSource(PolicySource.PLATFORM_DEFAULT);
                    policy.setActive(true);
                    return policy;
                });
    }


    private NotificationPolicy clonePolicy(
            NotificationPolicy base,
            Long companyId
    ) {
        NotificationPolicy copy = new NotificationPolicy();
        copy.setEvent(base.getEvent());
        copy.setCompanyId(companyId);
        copy.setSource(PolicySource.COMPANY_OVERRIDE);
        copy.setActive(base.isActive());

        base.getRecipients().forEach(r -> {
            NotificationPolicyRecipient nr = new NotificationPolicyRecipient();
            nr.setRecipient(r.getRecipient());
            nr.setPolicy(copy);

            r.getChannels().forEach(c -> {
                NotificationPolicyChannel nc = new NotificationPolicyChannel();
                nc.setChannel(c.getChannel());
                nc.setEnabled(c.isEnabled());
                nc.setTemplateCode(c.getTemplateCode());
                nc.setPriority(c.getPriority());
                nc.setRecipient(nr);
                nr.getChannels().add(nc);
            });

            copy.getRecipients().add(nr);
        });

        return copy;
    }


    private void syncRecipients(
            NotificationPolicy policy,
            List<NotificationRecipientPolicyDTO> dtoRecipients
    ) {
        policy.getRecipients().clear();

        dtoRecipients.forEach(rdto -> {
            NotificationPolicyRecipient recipient = new NotificationPolicyRecipient();
            recipient.setRecipient(rdto.getRecipient());
            recipient.setPolicy(policy);

            rdto.getChannels().forEach(cdto -> {
                NotificationPolicyChannel channel = new NotificationPolicyChannel();
                channel.setChannel(cdto.getChannel());
                channel.setEnabled(cdto.isEnabled());
                channel.setTemplateCode(cdto.getTemplateCode());
                channel.setPriority(cdto.getPriority());
                channel.setRecipient(recipient);

                recipient.getChannels().add(channel);
            });

            policy.getRecipients().add(recipient);
        });
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

        dto.setRecipients(
            entity.getRecipients()
                  .stream()
                  .map(this::toRecipientDto)
                  .toList()
        );

        return dto;
    }
    
    private NotificationRecipientPolicyDTO toRecipientDto(
            NotificationPolicyRecipient entity
    ) {
        NotificationRecipientPolicyDTO dto =
                new NotificationRecipientPolicyDTO();

        dto.setRecipient(entity.getRecipient());

        dto.setChannels(
            entity.getChannels()
                  .stream()
                  .map(this::toChannelDto)
                  .toList()
        );

        return dto;
    }
    private NotificationPolicyChannelDTO toChannelDto(
            NotificationPolicyChannel entity
    ) {
        NotificationPolicyChannelDTO dto =
                new NotificationPolicyChannelDTO();

        dto.setId(entity.getId());
        dto.setChannel(entity.getChannel());
        dto.setEnabled(entity.isEnabled());
        dto.setTemplateCode(entity.getTemplateCode());
        dto.setPriority(entity.getPriority());

        return dto;
    }


    private NotificationPolicyChannel toEntity(
            NotificationPolicyChannelDTO dto,
            NotificationPolicyRecipient recipient
    ) {
        NotificationPolicyChannel entity = new NotificationPolicyChannel();

        entity.setRecipient(recipient);   // ✅ correct owner
        entity.setChannel(dto.getChannel());
        entity.setEnabled(dto.isEnabled());
        entity.setTemplateCode(dto.getTemplateCode());
        entity.setPriority(dto.getPriority());

        return entity;
    }

}
