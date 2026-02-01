package com.org.bgv.company.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CompanyListResponseDTO {

    private Long id;
    private String companyName;

    private CompanyType companyType;
    private CompanyLegalType legalType;

    private String registrationNumber;

    private IndustryType industry;
    private CompanySize companySize;

    private String status;

    private String country;

    private String contactEmail;

    private LocalDate incorporationDate;
}
