package com.org.bgv.notifications.placeholder;

import com.org.bgv.notifications.dto.NotificationPlaceholder;

public interface PlaceholderResolver {

    boolean supports(NotificationPlaceholder placeholder);

    Object resolve(ResolutionContext context);
}
