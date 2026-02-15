package com.org.bgv.notifications.placeholder;

import org.springframework.stereotype.Component;

import com.org.bgv.company.entity.CompanyEmailSettings;
import com.org.bgv.entity.Company;
import com.org.bgv.notifications.dto.NotificationPlaceholder;

@Component
public class EmployerPlaceholderResolver implements PlaceholderResolver {

    @Override
    public boolean supports(NotificationPlaceholder p) {
        return p == NotificationPlaceholder.EMPLOYER_BRAND_NAME
            || p == NotificationPlaceholder.EMPLOYER_LEGAL_NAME
            || p == NotificationPlaceholder.EMPLOYER_SUPPORT_EMAIL;
    }

    @Override
    public Object resolve(ResolutionContext ctx) {

        Company company = ctx.getCompany();
        CompanyEmailSettings emailSettings = ctx.getCompanyEmailSettings();

        return switch (ctx.getCurrentPlaceholder()) {

            case EMPLOYER_BRAND_NAME ->
                    company.getCompanyName(); // brand = display name

            case EMPLOYER_LEGAL_NAME ->
                    company.getCompanyName() + " " + company.getLegalType();

            case EMPLOYER_SUPPORT_EMAIL ->
                    emailSettings != null
                        ? emailSettings.getSupportEmail()
                        : company.getContactEmail();

            default -> null;
        };
    }
}

