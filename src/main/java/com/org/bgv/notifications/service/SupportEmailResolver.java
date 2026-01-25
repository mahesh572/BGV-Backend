package com.org.bgv.notifications.service;

import org.springframework.stereotype.Service;

import com.org.bgv.company.entity.CompanyEmailSettings;
import com.org.bgv.company.repository.CompanyEmailSettingsRepository;
import com.org.bgv.entity.PlatformEmailSettings;
import com.org.bgv.repository.PlatformEmailSettingsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupportEmailResolver {

    private final CompanyEmailSettingsRepository companyRepo;
    private final PlatformEmailSettingsRepository platformRepo;

    public String resolve(Long companyId) {

        // 1️⃣ Company-level support email
        if (companyId != null) {
            return companyRepo.findByCompanyId(companyId)
                    .map(CompanyEmailSettings::getSupportEmail)
                    .filter(this::isValid)
                    .orElseGet(this::platformSupport);
        }

        // 2️⃣ Platform-level support email
        return platformSupport();
    }

    private String platformSupport() {
        return platformRepo.findActive()
                .map(PlatformEmailSettings::getSupportEmail)
                .filter(this::isValid)
                .orElse("support@bgv.com"); // final fallback
    }

    private boolean isValid(String email) {
        return email != null && !email.isBlank();
    }
}
