package com.org.bgv.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerInfoDTO {
    private String name;
    private String email;
    private String phone;
    private String companyName;
    private String companyId;
    private String hrContactPerson;
    private String hrEmail;
    private String hrPhone;
}
