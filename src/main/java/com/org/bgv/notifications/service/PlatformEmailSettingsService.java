package com.org.bgv.notifications.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.bgv.entity.PlatformEmailSettings;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.notifications.dto.EmailSettingsRequest;
import com.org.bgv.notifications.dto.EmailSettingsResponse;
import com.org.bgv.repository.PlatformEmailSettingsRepository;
import com.org.bgv.settings.dto.SmtpConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PlatformEmailSettingsService {

    private final PlatformEmailSettingsRepository repository;
    
    private final ObjectMapper objectMapper;
    
    private final EmailSenderResolver emailSenderResolver;

   
    public EmailSettingsResponse getActiveSettings() {
        return repository.findActive()
                .map(this::map)
                .orElseGet(() -> {
                    log.warn("No active platform email settings found, returning default empty settings");
                    return EmailSettingsResponse.builder().active(false).build();
                });
    }

    
    public EmailSettingsResponse saveOrUpdate(EmailSettingsRequest request) {

        // Deactivate existing active config
        repository.findActive().ifPresent(settings -> {
            settings.setActive(false);
            repository.save(settings);
        });

        Map<String, Object> smtpConfig = new HashMap();
        smtpConfig.put("host", request.getHost());
        smtpConfig.put("port", request.getPort());
        smtpConfig.put("username", request.getUsername());
        smtpConfig.put("password", request.getPassword()); // Encrypt before storing if required
        smtpConfig.put("auth", request.getAuth());
        smtpConfig.put("startTls", request.getStartTls());
        smtpConfig.put("protocol", request.getProtocol());

        String smtpConfigJson;
        try {
            smtpConfigJson = objectMapper.writeValueAsString(smtpConfig);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize SMTP configuration.", e);
        }

        PlatformEmailSettings settings = PlatformEmailSettings.builder()
                .fromName(request.getFromName())
                .fromEmail(request.getFromEmail())
                .replyToEmail(request.getReplyToEmail())
                .supportEmail(request.getSupportEmail())
                .smtpProvider(request.getSmtpProvider())
                .smtpConfigJson(smtpConfigJson)
                .active(request.isActive())
                .build();

        repository.save(settings);

        return map(settings);
    }

    
    public void activate(Long id) {
        repository.findActive().ifPresent(s -> s.setActive(false));

        PlatformEmailSettings settings = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Settings not found"));

        settings.setActive(true);
    }

   
    public void deactivate(Long id) {
        PlatformEmailSettings settings = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Settings not found"));

        settings.setActive(false);
    }

    private EmailSettingsResponse map(PlatformEmailSettings s) {

        EmailSettingsResponse.EmailSettingsResponseBuilder builder =
                EmailSettingsResponse.builder()
                        .id(s.getId())
                        .fromName(s.getFromName())
                        .fromEmail(s.getFromEmail())
                        .replyToEmail(s.getReplyToEmail())
                        .supportEmail(s.getSupportEmail())
                        .smtpProvider(s.getSmtpProvider())
                        .active(s.isActive());
                       // .verified(false);

        if (s.getSmtpConfigJson() != null && !s.getSmtpConfigJson().isBlank()) {
            try {
                SmtpConfig smtpConfig = objectMapper.readValue(
                        s.getSmtpConfigJson(),
                        SmtpConfig.class);

                builder.host(smtpConfig.getHost())
                        .port(smtpConfig.getPort())
                        .username(smtpConfig.getUsername())
                        // builder.password("********"); // Recommended instead of returning actual password
                        .password(smtpConfig.getPassword())
                        .auth(smtpConfig.isAuth())
                        .startTls(smtpConfig.isStarttls())
                        .protocol(smtpConfig.getProtocol());

            } catch (JsonProcessingException e) {
                log.error("Invalid SMTP config JSON for email settings id={}", s.getId(), e);
                // Don't fail the entire response
            }
        }

        return builder.build();
    }
    
    
    
}
