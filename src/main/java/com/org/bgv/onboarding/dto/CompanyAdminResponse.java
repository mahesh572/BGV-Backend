package com.org.bgv.onboarding.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyAdminResponse {

    private Long userId;

    private String email;

    private String role;

    private String status;

}