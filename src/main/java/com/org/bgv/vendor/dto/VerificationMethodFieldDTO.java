package com.org.bgv.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationMethodFieldDTO {

    private Long fieldId;

    private String fieldName;

    private String fieldLabel;

    private String fieldType;

    private Boolean requiredField;

    private Integer displayOrder;

    private String placeholder;
    
    private String value;
}