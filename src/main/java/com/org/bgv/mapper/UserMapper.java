package com.org.bgv.mapper;

import com.org.bgv.common.UserDto;
import com.org.bgv.dto.AddressDTO;
import com.org.bgv.dto.UserDetailsDto;

import com.org.bgv.entity.Address;
import com.org.bgv.entity.User;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class UserMapper implements BaseMapper<User, UserDetailsDto> {
   
	@Override
    public UserDetailsDto toDto(User entity) {
    	UserDetailsDto dto = new UserDetailsDto();
        dto.setUserId(entity.getUserId());
      //  dto.setFirstName(entity.getFirstName());
      //  dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
       // dto.setUserType(entity.getUserType());
      //  dto.setPhoneNumber(entity.getPhoneNumber());
		/*
		 * if (entity.getAddresses() != null && !entity.getAddresses().isEmpty()) {
		 * dto.setAddresses(entity.getAddresses().stream() .map(this::mapAddressDto)
		 * .collect(Collectors.toList())); }
		 */

        return dto;
    }
	
	public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }

        String fullName = (user.getFirstName() != null ? user.getFirstName() : "") + 
                         (user.getLastName() != null ? " " + user.getLastName() : "").trim();
        
        if (fullName.isEmpty()) {
            fullName = null;
        }

        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userType(user.getUserType())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .name(fullName)
                .phoneNumber(user.getPhoneNumber())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .profilePictureUrl(user.getProfilePictureUrl())
                .gender(user.getGender())
                .status(user.getStatus())
                .dateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null)
                .build();
    }


    @Override
    public User toEntity(UserDetailsDto dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setUserType(dto.getUserType());
        user.setIsVerified(Boolean.FALSE);

        if (dto.getAddresses() != null && !dto.getAddresses().isEmpty()) {
            user.setAddresses(dto.getAddresses().stream()
                .map(addr -> mapAddressToEntity(addr, user))
                .collect(Collectors.toList()));
        }

        return user;
    }
    
    private Address mapAddressToEntity(AddressDTO addr, User user) {
        return Address.builder()
            .addressLine1(addr.getAddressLine1())
            .addressLine2(addr.getAddressLine2())
            .city(addr.getCity())
            .state(addr.getState())
            .country(addr.getCountry())
            .postalCode(addr.getPostalCode())
            .isDefault(addr.isDefault())
            .addressType(addr.getAddressType())
            .user(user)
            .build();
    }
    private AddressDTO mapAddressDto(Address address) {
        AddressDTO addrDto = new AddressDTO();
        addrDto.setAddressLine1(address.getAddressLine1());
        addrDto.setAddressLine2(address.getAddressLine2());
        addrDto.setCity(address.getCity());
        addrDto.setState(address.getState());
        addrDto.setCountry(address.getCountry());
        addrDto.setPostalCode(address.getPostalCode());
        addrDto.setDefault(address.isDefault());
        addrDto.setAddressType(address.getAddressType());
        return addrDto;
    }
}
