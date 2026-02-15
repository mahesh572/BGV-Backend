package com.org.bgv.notifications.placeholder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.org.bgv.notifications.dto.NotificationPlaceholder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceholderEngine {

    private final List<PlaceholderResolver> resolvers;

    public Map<String, Object> resolveAll(
            Set<NotificationPlaceholder> placeholders,
            ResolutionContext context
    ) {

        Map<String, Object> values = new HashMap();

        for (NotificationPlaceholder placeholder : placeholders) {

            context.setCurrentPlaceholder(placeholder);

            for (PlaceholderResolver resolver : resolvers) {
                if (resolver.supports(placeholder)) {
                    Object value = resolver.resolve(context);
                    if (value != null) {
                        values.put(placeholder.key(), value);
                        break;
                    }
                }
            }
        }
        return values;
    }
}
