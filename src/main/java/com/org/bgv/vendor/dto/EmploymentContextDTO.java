package com.org.bgv.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentContextDTO {
    private String verificationStage; // hr_contact, payslip_verification, background_check
    private String hrContactDetails;
    private String companyWebsite;
    private Boolean isCompanyActive;
    private String verificationMethod; // email, phone, portal
    private Boolean requiresReferenceCheck;
    private Boolean requiresExitInterview;
    private String companySize;
    private String industryType;
}
