package com.org.bgv.bgvpackage.dto;

import java.math.BigDecimal;

import com.org.bgv.enums.PricingType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployerCheckPricingResponse {

    private Long id;

    private Long companyId;
    private String companyName;

    private Long checkCategoryId;
    private String checkCategoryName;
    private String checkCategoryCode;

    private Long ruleTypeId;
    private String ruleTypeName;
    private String ruleTypeCode;

    private PricingType pricingType;

    private BigDecimal  unitPrice;
    private Double minCharge;
    private Double maxCharge;

    private Boolean active;
}
