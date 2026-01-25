package com.org.bgv.notifications.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.entity.PlatformEmailSettings;
import com.org.bgv.notifications.dto.EmailSettingsRequest;
import com.org.bgv.notifications.dto.EmailSettingsResponse;
import com.org.bgv.repository.PlatformEmailSettingsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PlatformEmailSettingsService {

    private final PlatformEmailSettingsRepository repository;

   
    public EmailSettingsResponse getActiveSettings() {
        return repository.findActive()
                .map(this::map)
                .orElseGet(() -> {
                    log.warn("No active platform email settings found, returning default empty settings");
                    return EmailSettingsResponse.builder().active(false).build();
                });
    }

    
    public EmailSettingsResponse saveOrUpdate(EmailSettingsRequest request) {

        // deactivate existing active config
        repository.findActive().ifPresent(s -> s.setActive(false));

        PlatformEmailSettings settings = PlatformEmailSettings.builder()
                .fromName(request.getFromName())
                .fromEmail(request.getFromEmail())
                .replyToEmail(request.getReplyToEmail())
                .supportEmail(request.getSupportEmail())
                .smtpProvider(request.getSmtpProvider())
                .smtpConfigJson(request.getSmtpConfigJson())
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
        return EmailSettingsResponse.builder()
                .id(s.getId())
                .fromName(s.getFromName())
                .fromEmail(s.getFromEmail())
                .replyToEmail(s.getReplyToEmail())
                .supportEmail(s.getSupportEmail())
                .smtpProvider(s.getSmtpProvider())
                .active(s.isActive())
                .verified(false) // later
                .build();
    }
}
