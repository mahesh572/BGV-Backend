package com.org.bgv.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceholderDTO {

    /**
     * Placeholder key used in template
     * Example: candidateName
     */
    private String key;

    /**
     * Human readable label
     * Example: Candidate Name
     */
    private String label;

    /**
     * Token inserted into editor
     * Example: {{candidateName}}
     */
    private String token;
}
