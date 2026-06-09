package com.org.bgv.onboarding.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDynamicFieldDto {

    private Long definitionId;

    private String attributeCode;

    private String attributeLabel;

    private String controlType;

    private String dataType;

    private Boolean required;

    private Integer displayOrder;

    private String value;

    private List<CompanyDynamicFieldOptionDto> options;
}