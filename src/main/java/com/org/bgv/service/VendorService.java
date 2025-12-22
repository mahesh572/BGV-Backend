package com.org.bgv.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.org.bgv.common.Status;
import com.org.bgv.dto.VendorDTO;
import com.org.bgv.entity.CheckType;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.entity.Vendor;
import com.org.bgv.entity.VendorCheckMapping;
import com.org.bgv.repository.CheckTypeRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.RoleRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.UserRoleRepository;
import com.org.bgv.repository.VendorCheckMappingRepository;
import com.org.bgv.repository.VendorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorService {
    
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final VendorRepository vendorRepository;
    private final CheckTypeRepository checkTypeRepository;
    private final VendorCheckMappingRepository vendorCheckMappingRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public Boolean createVendor(VendorDTO vendorDTO) {
        Boolean isSuccess = Boolean.FALSE;
        if(vendorDTO != null) {
            // Create and save User
            User user = User.builder()
                .email(vendorDTO.getEmail())
                .password(passwordEncoder.encode("123456"))
                .userType(Status.USER_TYPE_VENDOR)
                .dateOfBirth(vendorDTO.getDateOfBirth())
                .build();
            
            user = userRepository.save(user);
            
            // Create and save Vendor
            Vendor vendor = Vendor.builder()
                .addressLine1(vendorDTO.getAddressLine1())
                .addressLine2(vendorDTO.getAddressLine2())
                .availability(vendorDTO.getAvailability())
                .businessName(vendorDTO.getBusinessName())
                .businessType(vendorDTO.getBusinessType())
                .city(vendorDTO.getCity())
                .country(vendorDTO.getCountry())
                .description(vendorDTO.getDescription())
                .experience(vendorDTO.getExperience())
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
            
            Profile profile =Profile.builder()
              .firstName(vendorDTO.getFirstName())
              .lastName(vendorDTO.getLastName())
              .phoneNumber(vendorDTO.getPhone())
              .gender(vendorDTO.getGender())
            .build();
            
            profileRepository.save(profile);
            
            /*
            // Assign ROLE_VENDOR to the user
            Role vendorRole = roleRepository.findByName("ROLE_VENDOR")
                    .orElseThrow(() -> new RuntimeException("ROLE_VENDOR not found"));
            
            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(vendorRole)
                    .build();
            
            userRoleRepository.save(userRole);
            */
            
            // Handle services provided - FIXED: Use final reference
            if (vendorDTO.getServicesProvided() != null && !vendorDTO.getServicesProvided().isEmpty()) {
            	saveVendorServicesWithStream(vendor, vendorDTO.getServicesProvided());
            }
            
            // create profile
            
            isSuccess = Boolean.TRUE;
        }
        return isSuccess;
    }
    
    // Alternative solution using stream with effectively final variable
    private void saveVendorServicesWithStream(final Vendor vendor, List<Long> serviceIds) {
        List<VendorCheckMapping> mappings = serviceIds.stream()
            .map(checkTypeId -> {
                CheckType checkType = checkTypeRepository.findById(checkTypeId)
                        .orElseThrow(() -> new RuntimeException("CheckType not found: " + checkTypeId));
                
                return VendorCheckMapping.builder()
                        .vendor(vendor) // vendor is effectively final now
                        .checkType(checkType)
                        .isActive(true)
                        .build();
            })
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        
        vendorCheckMappingRepository.saveAll(mappings);
    }
    
    
}