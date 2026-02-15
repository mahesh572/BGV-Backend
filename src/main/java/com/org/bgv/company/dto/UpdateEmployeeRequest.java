package com.org.bgv.company.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmployeeRequest {

    private String firstName;
    private String lastName;
    private String phoneNumber;

    private String designation;
    private String department;
    private String employmentType;
    private String status; // ACTIVE / INACTIVE
}

