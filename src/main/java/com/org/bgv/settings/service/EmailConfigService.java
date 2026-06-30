package com.org.bgv.settings.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.bgv.entity.PlatformEmailSettings;
import com.org.bgv.repository.PlatformEmailSettingsRepository;
import com.org.bgv.settings.dto.SmtpConfig;

@Service
public class EmailConfigService {

    private final PlatformEmailSettingsRepository repository;
    private final ObjectMapper objectMapper;

    public EmailConfigService(PlatformEmailSettingsRepository repository,
                              ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public SmtpConfig getActiveConfig() {
        PlatformEmailSettings settings = repository.findActive()
                .orElseThrow(() -> new RuntimeException("No active email config found"));

        try {
            return objectMapper.readValue(settings.getSmtpConfigJson(), SmtpConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid SMTP config JSON", e);
        }
    }
}