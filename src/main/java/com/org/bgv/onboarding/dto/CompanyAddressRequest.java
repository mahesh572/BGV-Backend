package com.org.bgv.onboarding.dto;

import com.org.bgv.enums.CompanyAddressType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyAddressRequest {

    private CompanyAddressType addressType;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private Long stateId;

    private Long countryId;

    private String zipCode;

    private boolean primaryAddress;
}
