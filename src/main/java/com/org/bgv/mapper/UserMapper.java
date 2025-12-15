package com.org.bgv.mapper;

import com.org.bgv.common.UserDto;
import com.org.bgv.dto.AddressDTO;
import com.org.bgv.dto.BasicdetailsDTO;
import com.org.bgv.dto.UserDetailsDto;

import com.org.bgv.entity.Address;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.User;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.service.DocumentService;
import com.org.bgv.service.EducationService;
import com.org.bgv.service.IdentityProofService;
import com.org.bgv.service.ProfileAddressService;
import com.org.bgv.service.WorkExperienceService;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserMapper implements BaseMapper<User, UserDto> {
   
	 private final ProfileRepository profileRepository;
	
	@Override
    public UserDto toDto(User entity) {
		
		Profile profile = profileRepository.findByUserUserId(entity.getUserId());
		        		
		
		UserDto dto = new UserDto();
        dto.setUserId(entity.getUserId());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setUserType(entity.getUserType());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setPasswordResetrequired(entity.getPasswordResetrequired()==null?Boolean.FALSE:entity.getPasswordResetrequired());

        return dto;
    }
	
	public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        Profile profile = profileRepository.findByUserUserId(user.getUserId());
        String fullName = (profile.getFirstName() != null ? profile.getFirstName() : "") + 
                         (profile.getLastName() != null ? " " + profile.getLastName() : "").trim();
        
        if (fullName.isEmpty()) {
            fullName = null;
        }

        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userType(user.getUserType())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .name(fullName)
                .phoneNumber(profile.getPhoneNumber())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .profilePictureUrl(user.getProfilePictureUrl())
                .gender(profile.getGender())
                .status(user.getStatus())
                .dateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth() : null)
                .build();
    }


    @Override
    public User toEntity(UserDto dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
       // user.setFirstName(dto.getFirstName());
      //  user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
      //  user.setPhoneNumber(dto.getPhoneNumber());
       // user.setDateOfBirth(dto.getDateOfBirth());
        user.setUserType(dto.getUserType());
        user.setIsVerified(Boolean.FALSE);
        /*
        if (dto.getAddresses() != null && !dto.getAddresses().isEmpty()) {
            user.setAddresses(dto.getAddresses().stream()
                .map(addr -> mapAddressToEntity(addr, user))
                .collect(Collectors.toList()));
        }
         */
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
    public BasicdetailsDTO mapUserDTOToBasicdetails(UserDto userDto) {
    	
    	return BasicdetailsDTO.builder()
    			.firstName(userDto.getFirstName())
    			.lastName(userDto.getLastName())
    			.gender(userDto.getGender())
    			.phone(userDto.getPhoneNumber())
    			.dateOfBirth(userDto.getDateOfBirth())
    			.email(userDto.getEmail())
    			.user_id(userDto.getUserId())
    			.build();
    	
    	
    	
    }
}
