package com.org.bgv.notifications.service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.notifications.dto.NotificationPlaceholder;
import com.org.bgv.notifications.dto.PlaceholderDTO;
import com.org.bgv.notifications.dto.PlaceholderRolePolicy;
import com.org.bgv.notifications.dto.TemplateUserRole;

@Service
public class PlaceholderService {

    public List<PlaceholderDTO> getAllowedPlaceholders(
            TemplateUserRole role
    ) {
        return Arrays.stream(NotificationPlaceholder.values())
            .filter(p ->
                PlaceholderRolePolicy
                    .valueOf(p.name())
                    .allowedFor(role)
            )
            .map(p -> new PlaceholderDTO(
                p.key(),
                p.label(),
                "{{" + p.key() + "}}"
            ))
            .toList();
    }

    public Set<String> allowedKeys(TemplateUserRole role) {
        return getAllowedPlaceholders(role)
                .stream()
                .map(PlaceholderDTO::getKey)
                .collect(Collectors.toSet());
    }
}

