package com.org.bgv.mapper;

import com.org.bgv.common.UserDto;
import com.org.bgv.dto.AddressDTO;
import com.org.bgv.dto.BasicDetailsDTO;
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
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserMapper implements BaseMapper<User, UserDto> {

    @Override
    public UserDto toDto(User user) {

        if (user == null) {
            return null;
        }

        Profile profile = user.getProfile();

        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userType(user.getUserType())
                .firstName(profile != null ? profile.getFirstName() : null)
                .lastName(profile != null ? profile.getLastName() : null)
                .fullName(getFullName(profile))
                .phoneNumber(profile != null ? profile.getPhoneNumber() : null)
                .gender(profile != null ? profile.getGender() : null)
                .profilePictureUrl(user.getProfilePictureUrl())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .dateOfBirth(user.getDateOfBirth())
                .passwordResetrequired(
                        Boolean.TRUE.equals(user.getPasswordResetrequired()))
                .build();
    }

    @Override
    public User toEntity(UserDto dto) {

        if (dto == null) {
            return null;
        }

        return User.builder()
                .userId(dto.getUserId())
                .email(dto.getEmail())
                .userType(dto.getUserType())
                .isVerified(Boolean.FALSE)
                .build();
    }

    public BasicDetailsDTO mapUserDtoToBasicDetails(UserDto userDto) {

        if (userDto == null) {
            return null;
        }

        return BasicDetailsDTO.builder()
                .user_id(userDto.getUserId())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .gender(userDto.getGender())
                .phone(userDto.getPhoneNumber())
                .dateOfBirth(userDto.getDateOfBirth())
                .email(userDto.getEmail())
                .build();
    }

    public AddressDTO mapAddressDto(Address address) {

        if (address == null) {
            return null;
        }

        return AddressDTO.builder()
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .isDefault(address.isDefault())
                .addressType(address.getAddressType())
                .build();
    }

    public Address mapAddressToEntity(AddressDTO dto, User user) {

        if (dto == null) {
            return null;
        }

        return Address.builder()
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .zipCode(dto.getZipCode())
                .isDefault(dto.isDefault())
                .addressType(dto.getAddressType())
                .user(user)
                .build();
    }
    
    public String getFullName(Profile profile) {
        return Stream.of(profile != null ? profile.getFirstName() : null, profile != null ? profile.getLastName() : null)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "));
    }
}

