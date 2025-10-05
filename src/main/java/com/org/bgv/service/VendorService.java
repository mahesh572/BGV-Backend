package com.org.bgv.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.org.bgv.common.Status;
import com.org.bgv.config.JwtUtil;
import com.org.bgv.dto.VendorDTO;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.entity.Vendor;
import com.org.bgv.mapper.UserMapper;
import com.org.bgv.repository.RoleRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.UserRoleRepository;
import com.org.bgv.repository.VendorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorService {
	
	private final PasswordEncoder passwordEncoder; // configure this as a @Bean
	private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final VendorRepository vendorRepository;

    @Transactional
	public Boolean createVendor(VendorDTO vendorDTO) {
		Boolean isSuccess = Boolean.FALSE;
		if(vendorDTO!=null) {
			User user = User.builder()
			.firstName(vendorDTO.getFirstName())
			.lastName(vendorDTO.getLastName())
			.phoneNumber(vendorDTO.getPhone())
			.email(vendorDTO.getEmail())
			.password(passwordEncoder.encode("1234"))
			.userType(Status.USER_TYPE_VENDOR)
			.build();
			
			user = userRepository.save(user);
			
			Vendor vendor = Vendor.builder()
			.addressLine1(vendorDTO.getAddressLine1())
			.addressLine2(vendorDTO.getAddressLine2())
			.availability(vendorDTO.getAvailability())
			.businessName(vendorDTO.getBusinessName())
			.businessType(vendorDTO.getBusinessType())
			.city(vendorDTO.getCity())
			.country(vendorDTO.getCountry())
			.dateOfBirth(vendorDTO.getDateOfBirth())
			.description(vendorDTO.getDescription())
			.experience(vendorDTO.getExperience())
			.gender(vendorDTO.getGender())
			.hourlyRate(vendorDTO.getHourlyRate())
			.zipCode(vendorDTO.getZipCode())
			.website(vendorDTO.getWebsite())
			.vendorType(vendorDTO.getVendorType())
			.user(user)
			.taxId(vendorDTO.getTaxId())
			.status(Status.PENDING)
			.state(vendorDTO.getState())
			
			
			.build();
			vendor = vendorRepository.save(vendor);
			
			// Assign ROLE_ADMIN to the admin user
            Role vendorRole = roleRepository.findByName("ROLE_VENDOR")
                    .orElseThrow(() -> new RuntimeException("ROLE_VENDOR not found"));
            
            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(vendorRole)
                    .build();
            
            userRoleRepository.save(userRole);
            
            isSuccess = Boolean.TRUE;
		}
		return isSuccess;
	}
	
	
}
