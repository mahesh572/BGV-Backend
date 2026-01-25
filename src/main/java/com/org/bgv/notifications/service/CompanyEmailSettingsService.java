package com.org.bgv.notifications.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.company.entity.CompanyEmailSettings;
import com.org.bgv.company.repository.CompanyEmailSettingsRepository;
import com.org.bgv.notifications.dto.EmailSettingsRequest;
import com.org.bgv.notifications.dto.EmailSettingsResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyEmailSettingsService {

    private final CompanyEmailSettingsRepository repository;

    public EmailSettingsResponse get(Long companyId) {
        return repository.findByCompanyId(companyId)
                .map(this::map)
                .orElseGet(this::emptyResponse);
    }

    private EmailSettingsResponse emptyResponse() {
        return EmailSettingsResponse.builder()
                .fromName("")
                .fromEmail("")
                .replyToEmail("")
                .supportEmail("")
                .smtpProvider("")
               // .smtpConfigJson("")
                .active(false)
                .build();
    }

    
    public EmailSettingsResponse saveOrUpdate(Long companyId, EmailSettingsRequest request) {

        CompanyEmailSettings settings = repository.findByCompanyId(companyId)
                .orElse(
                    CompanyEmailSettings.builder()
                        .companyId(companyId)
                        .build()
                );

        settings.setFromName(request.getFromName());
        settings.setFromEmail(request.getFromEmail());
        settings.setReplyToEmail(request.getReplyToEmail());
        settings.setSupportEmail(request.getSupportEmail());
        settings.setSmtpProvider(request.getSmtpProvider());
        settings.setSmtpConfigJson(request.getSmtpConfigJson());
        settings.setActive(request.isActive());

        repository.save(settings);
        return map(settings);
    }

    
    public void activate(Long companyId) {
        CompanyEmailSettings settings = repository.findByCompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Settings not found"));

        settings.setActive(true);
    }

    
    public void deactivate(Long companyId) {
        CompanyEmailSettings settings = repository.findByCompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Settings not found"));

        settings.setActive(false);
    }

    private EmailSettingsResponse map(CompanyEmailSettings s) {
        return EmailSettingsResponse.builder()
                .id(s.getId())
                .fromName(s.getFromName())
                .fromEmail(s.getFromEmail())
                .replyToEmail(s.getReplyToEmail())
                .supportEmail(s.getSupportEmail())
                .smtpProvider(s.getSmtpProvider())
                .active(s.isActive())
                .verified(s.isVerified())
                .build();
    }
}
