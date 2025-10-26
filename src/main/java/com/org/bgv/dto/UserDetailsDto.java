package com.org.bgv.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class UserDetailsDto {
		private Long userId;
	    private String name;
	    private String email;
	    private String password;
	    private String firstName;
	    private String lastName;
	    private String phoneNumber;
	    private LocalDate dateOfBirth;
	    private String profilePictureUrl;
	    private String userType;
	    private Boolean isActive;
	    private List<AddressDTO> addresses;
	    private List<String> roles;
	    private List<String> permissions;
	    private Long profileId;
	    private Long companyId;
}
