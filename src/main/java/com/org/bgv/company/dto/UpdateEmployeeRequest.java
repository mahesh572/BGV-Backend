package com.org.bgv.company.dto;

import com.org.bgv.user.enums.UserStatus;

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
    private UserStatus status; // ACTIVE / INACTIVE
}

