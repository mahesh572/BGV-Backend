package com.org.bgv.company.dto;

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
    private String gender;
    private String role;
    private String status;

}
