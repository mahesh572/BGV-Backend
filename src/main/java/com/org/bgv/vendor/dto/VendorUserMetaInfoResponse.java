package com.org.bgv.vendor.dto;


import java.util.List;

import com.org.bgv.commom.dto.StateRegionDto;
import com.org.bgv.commom.dto.VerificationServiceResponse;
import com.org.bgv.role.dto.RoleDetailDto;
import com.org.bgv.role.dto.RoleResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorUserMetaInfoResponse {

    private List<RoleDetailDto> roles;

    private List<StateRegionDto> regions;

    private List<VerificationServiceResponse> servicesProvided;

}
