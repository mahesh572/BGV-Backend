package com.org.bgv.onboarding.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCompanyAdminRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;
    
    private Long roleId;

}