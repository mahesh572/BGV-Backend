package com.org.bgv.company.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateEmployeeRequest {

    private Long userId; // existing global user

    private String employeeCode;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;

    private String designation;
    private String department;
    private String employmentType; // FULL_TIME, CONTRACT
}

