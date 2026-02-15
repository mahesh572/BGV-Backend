package com.org.bgv.notifications.placeholder;

import org.springframework.stereotype.Component;

import com.org.bgv.notifications.dto.NotificationPlaceholder;

@Component
public class RuntimePlaceholderResolver implements PlaceholderResolver {

    @Override
    public boolean supports(NotificationPlaceholder p) {
        return true; // fallback resolver
    }

    @Override
    public Object resolve(ResolutionContext ctx) {
        return ctx.getRuntimeValues().get(ctx.getCurrentPlaceholder().key());
    }
}
