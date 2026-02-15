package com.org.bgv.notifications.placeholder;

import org.springframework.stereotype.Component;

import com.org.bgv.entity.PlatformConfig;
import com.org.bgv.entity.PlatformEmailSettings;
import com.org.bgv.notifications.dto.NotificationPlaceholder;

@Component
public class PlatformPlaceholderResolver implements PlaceholderResolver {

    @Override
    public boolean supports(NotificationPlaceholder p) {
        return p == NotificationPlaceholder.PLATFORM_BRAND_NAME
            || p == NotificationPlaceholder.PLATFORM_LEGAL_NAME
            || p == NotificationPlaceholder.PLATFORM_SUPPORT_EMAIL;
    }

    @Override
    public Object resolve(ResolutionContext ctx) {

        PlatformConfig config = ctx.getPlatformConfig();
        PlatformEmailSettings emailSettings = ctx.getPlatformEmailSettings();

        return switch (ctx.getCurrentPlaceholder()) {

            case PLATFORM_BRAND_NAME ->
                    config.getPlatformBrandName();

            case PLATFORM_LEGAL_NAME ->
                    config.getPlatformLegalName();

            case PLATFORM_SUPPORT_EMAIL ->
                    emailSettings != null
                        ? emailSettings.getSupportEmail()
                        : config.getSupportEmail();

            default -> null;
        };
    }
}

