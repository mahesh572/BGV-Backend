package com.org.bgv.company.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyRegistrationResponse {
   
    private Long companyId;
    private Long adminUserId;
    private String companyName;
}