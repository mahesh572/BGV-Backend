package com.org.bgv.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailTemplateOptionDTO {

    private String templateCode;
    private String displayName;
    private String description;
}
