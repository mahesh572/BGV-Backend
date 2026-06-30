package com.org.bgv.company.dto;

import com.org.bgv.user.enums.Gender;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonDTO {
	private Long id;
	private String firstName;
    private String lastName;
    private String email;
    private String mobileNo;
    private Gender gender;
    private String role;
    private String status;

}
