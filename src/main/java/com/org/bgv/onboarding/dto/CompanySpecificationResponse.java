package com.org.bgv.onboarding.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanySpecificationResponse {

    private Long definitionId;

    private String attributeCode;

    private String attributeName;

    private String attributeLabel;

    private String dataType;

    private String controlType;

    private Boolean required;

    private Integer displayOrder;

    private String value;
}