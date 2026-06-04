package com.org.bgv.vendor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmploymentVerificationData {

    private String companyName;
    private String employeeId;
    private String designation;
    private String department;
    private String employmentPeriod;
    private String hrEmail;
}
