package com.org.bgv.onboarding.dto;


import java.time.LocalDate;

import com.org.bgv.company.dto.CompanyLegalType;
import com.org.bgv.company.dto.CompanySize;
import com.org.bgv.company.dto.CompanyType;
import com.org.bgv.company.dto.IndustryType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCompanyRequest {

    @NotBlank
    private String companyName;

    @NotNull
    private CompanyType companyType;

    @NotNull
    private CompanyLegalType legalType;

    @NotBlank
    private String registrationNumber;

    private String taxId;

    @NotNull
    private LocalDate incorporationDate;

    @NotNull
    private IndustryType industry;

    @NotNull
    private CompanySize companySize;

    private String website;

    private String linkedinProfile;
    
    private String tanNumber;
}