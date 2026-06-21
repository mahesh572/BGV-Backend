package com.org.bgv.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyContactRequest {

    private ContactType contactType;

    private String name;

    private String title;

    private String email;

    private String phone;

    private boolean primaryContact;
}
