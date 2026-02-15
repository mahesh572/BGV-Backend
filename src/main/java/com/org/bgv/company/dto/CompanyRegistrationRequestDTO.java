package com.org.bgv.company.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class CompanyRegistrationRequestDTO {
    
    // Company Information
    private String companyName;
    private CompanyType companyType;
    private CompanyLegalType legalType;
    private String registrationNumber;
    private String taxId;
    private LocalDate incorporationDate;
    private IndustryType industry;
    private CompanySize companySize;
    private String website;
    private String description;
    
    // Contact Information
    private String contactPersonName;
    private String contactPersonTitle;
    private String contactEmail;
    private String contactPhone;
    
    // Address Information
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    
    // Additional Information
    private String linkedinProfile;
    private String status;
    private Long id;
    
   
}