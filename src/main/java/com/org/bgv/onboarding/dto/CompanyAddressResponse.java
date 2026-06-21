package com.org.bgv.onboarding.dto;

import com.org.bgv.enums.CompanyAddressType;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CompanyAddressResponse {

    private Long id;

    private CompanyAddressType addressType;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String country;

    private String zipCode;

    private boolean primaryAddress;
}
