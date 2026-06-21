package com.org.bgv.onboarding.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanySpecificationRequest {

    // key = attributeCode
    // value = entered value
  //  private Map<String, String> attributes;
	
	private Long definitionId;

    private String value;
}