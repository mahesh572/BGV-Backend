package com.org.bgv.company.dto;

import java.time.LocalDate;

import com.org.bgv.user.enums.Gender;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeDTO {

	private Long employeeId;

    private String firstName;

    private String lastName;

    private String emailAddress;

    private String phoneNumber;

    private String designation;

    private String department;

    private String employmentType; // FULL_TIME, CONTRACT, INTERN

    private String status; // ACTIVE, INACTIVE

    private LocalDate dateOfBirth;

    private Gender gender;

    private String nationality;

    // Optional: add companyId if needed
    private Long companyId;
    
    private Long userId;
	
	
}
