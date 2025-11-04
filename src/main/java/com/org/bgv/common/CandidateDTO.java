package com.org.bgv.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CandidateDTO {
	private Long id;
	private String firstName;
    private String lastName;
    private String email;
    private String mobileNo;
    private String gender;
    private String role;
    private String status;
    private String sourceType;
    private Long companyId;
}
