package com.org.bgv.notifications.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.bgv.company.entity.CompanyEmailSettings;
import com.org.bgv.company.repository.CompanyEmailSettingsRepository;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.notifications.dto.EmailSettingsRequest;
import com.org.bgv.notifications.dto.EmailSettingsResponse;
import com.org.bgv.settings.dto.SmtpConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CompanyEmailSettingsService {

    private final CompanyEmailSettingsRepository repository;
    private final ObjectMapper objectMapper;

    /**
     * Get company email settings
     */
    public EmailSettingsResponse get(Long companyId) {

        return repository.findByCompanyId(companyId)
                .map(this::map)
                .orElseGet(() -> {
                    log.warn("No email settings found for companyId={}", companyId);
                    return emptyResponse();
                });
    }

    /**
     * Save or update company email settings
     */
    public EmailSettingsResponse saveOrUpdate(Long companyId, EmailSettingsRequest request) {

        CompanyEmailSettings settings = repository.findByCompanyId(companyId)
                .orElse(
                        CompanyEmailSettings.builder()
                                .companyId(companyId)
                                .verified(false)
                                .build()
                );

        Map<String, Object> smtpConfig = new HashMap<>();
        smtpConfig.put("host", request.getHost());
        smtpConfig.put("port", request.getPort());
        smtpConfig.put("username", request.getUsername());
        smtpConfig.put("password", request.getPassword()); // Encrypt if required
        smtpConfig.put("auth", request.getAuth());
        smtpConfig.put("startTls", request.getStartTls());
        smtpConfig.put("protocol", request.getProtocol());

        String smtpConfigJson;
        try {
            smtpConfigJson = objectMapper.writeValueAsString(smtpConfig);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Unable to serialize SMTP configuration.");
        }

        settings.setFromName(request.getFromName());
        settings.setFromEmail(request.getFromEmail());
        settings.setReplyToEmail(request.getReplyToEmail());
        settings.setSupportEmail(request.getSupportEmail());
        settings.setSmtpProvider(request.getSmtpProvider());
        settings.setSmtpConfigJson(smtpConfigJson);
        settings.setActive(request.isActive());

        // Configuration changed, needs re-verification
        settings.setVerified(false);

        repository.save(settings);

        log.info("Company email settings saved successfully for companyId={}", companyId);

        return map(settings);
    }

    /**
     * Activate email settings
     */
    public void activate(Long companyId) {

        CompanyEmailSettings settings = repository.findByCompanyId(companyId)
                .orElseThrow(() ->
                        new BusinessException("Email settings not found."));

        settings.setActive(true);

        repository.save(settings);

        log.info("Company email settings activated for companyId={}", companyId);
    }

    /**
     * Deactivate email settings
     */
    public void deactivate(Long companyId) {

        CompanyEmailSettings settings = repository.findByCompanyId(companyId)
                .orElseThrow(() ->
                        new BusinessException("Email settings not found."));

        settings.setActive(false);

        repository.save(settings);

        log.info("Company email settings deactivated for companyId={}", companyId);
    }

    /**
     * Empty response when no configuration exists
     */
    private EmailSettingsResponse emptyResponse() {

        return EmailSettingsResponse.builder()
                .fromName("")
                .fromEmail("")
                .replyToEmail("")
                .supportEmail("")
                .smtpProvider("")
                .active(false)
                .verified(false)
                .build();
    }

    /**
     * Entity -> Response
     */
    private EmailSettingsResponse map(CompanyEmailSettings settings) {

        EmailSettingsResponse.EmailSettingsResponseBuilder builder =
                EmailSettingsResponse.builder()
                        .id(settings.getId())
                        .fromName(settings.getFromName())
                        .fromEmail(settings.getFromEmail())
                        .replyToEmail(settings.getReplyToEmail())
                        .supportEmail(settings.getSupportEmail())
                        .smtpProvider(settings.getSmtpProvider())
                        .active(settings.isActive())
                        .verified(settings.isVerified());

        if (settings.getSmtpConfigJson() != null &&
                !settings.getSmtpConfigJson().isBlank()) {

            try {

                SmtpConfig smtpConfig = objectMapper.readValue(
                        settings.getSmtpConfigJson(),
                        SmtpConfig.class);

                builder.host(smtpConfig.getHost())
                        .port(smtpConfig.getPort())
                        .username(smtpConfig.getUsername())
                        // Prefer returning null or "********" instead of actual password
                        .password(smtpConfig.getPassword())
                        .auth(smtpConfig.isAuth())
                        .startTls(smtpConfig.isStarttls())
                        .protocol(smtpConfig.getProtocol());

            } catch (JsonProcessingException e) {

                log.error(
                        "Invalid SMTP configuration for companyId={}",
                        settings.getCompanyId(),
                        e
                );
            }
        }

        return builder.build();
    }
}