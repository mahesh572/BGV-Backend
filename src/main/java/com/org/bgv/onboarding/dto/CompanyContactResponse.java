package com.org.bgv.onboarding.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyContactResponse {

    private Long id;

    private ContactType contactType;

    private String name;

    private String title;

    private String email;

    private String phone;

    private boolean primaryContact;
}
