package com.org.bgv.notifications.service;

import org.springframework.stereotype.Service;

import com.org.bgv.company.entity.CompanyEmailSettings;
import com.org.bgv.company.repository.CompanyEmailSettingsRepository;
import com.org.bgv.entity.PlatformEmailSettings;
import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.repository.PlatformEmailSettingsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailSenderResolver {

    private final PlatformEmailSettingsRepository platformRepo;
    private final CompanyEmailSettingsRepository companyRepo;

    public String resolveFrom(NotificationContext context) {

        if (context.getCompanyId() != null) {
            return companyRepo.findByCompanyId(context.getCompanyId())
                    .map(CompanyEmailSettings::getFromEmail)
                    .orElseGet(this::platformFrom);
        }

        return platformFrom();
    }

    private String platformFrom() {
        return platformRepo.findActive()
                .map(PlatformEmailSettings::getFromEmail)
                .orElse("no-reply@bgv.com");
    }
}
