package com.org.bgv.vendor.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeclaredEmploymentInfoDTO {
    private String companyName;
    private String designation;
    private String department;
    private String employmentType; // full_time, part_time, contract
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isCurrentEmployment;
    private String location;
    private String reportingManager;
    private String employeeId;
    private String reasonForLeaving;
    private String lastDrawnSalary;
    private LocalDateTime declaredOn;
    private String verificationMethod;
    private List<String> responsibilities;
}
