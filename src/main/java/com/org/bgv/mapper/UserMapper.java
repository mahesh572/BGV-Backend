package com.org.bgv.mapper;

import com.org.bgv.dto.AddressDTO;
import com.org.bgv.dto.UserDto;
import com.org.bgv.entity.Address;
import com.org.bgv.entity.User;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class UserMapper implements BaseMapper<User, UserDto> {
    @Override
    public UserDto toDto(User entity) {
        UserDto dto = new UserDto();
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

    @Override
    public User toEntity(UserDto dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
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
