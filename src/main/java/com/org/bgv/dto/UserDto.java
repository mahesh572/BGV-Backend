package com.org.bgv.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserDto {
		private Long userId;
	    private String name;
	    private String email;
	    private String password;
	    private String firstName;
	    private String lastName;
	    private String phoneNumber;
	    private String profilePictureUrl;
	    private String userType;
	    private List<AddressDTO> addresses;
	    private List<String> roles;
	    private List<String> permissions;
}
