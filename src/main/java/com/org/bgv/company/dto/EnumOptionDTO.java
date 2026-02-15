package com.org.bgv.company.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnumOptionDTO {
    private String key;        // ENUM name (backend value)
    private String displayName; // UI label
}
