package com.org.bgv.global.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.repository.AddressRepository;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.candidate.service.AddressService;
import com.org.bgv.dto.AddressDTO;
import com.org.bgv.entity.Address;
import com.org.bgv.entity.User;
import com.org.bgv.global.dto.UserAddressDTO;
import com.org.bgv.global.entity.UserAddress;
import com.org.bgv.global.repository.UserAddressRepository;
import com.org.bgv.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAddressService {
	
	 private final UserAddressRepository useraddressRepository;
	 private final UserRepository userRepository;
	 
	 
	 public UserAddressDTO createAddress(Long userId,UserAddressDTO addressDTO) {
	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new RuntimeException("Candidate not found: " + userId));
	        
	        UserAddress address = mapToEntity(addressDTO, user);
	        UserAddress savedAddress = useraddressRepository.save(address);
	        return mapToDTO(savedAddress);
	    }
	 
	 public List<UserAddressDTO> getAddressesByUser(Long userId) {
	        List<UserAddress> addresses = useraddressRepository.findByUser_UserId(userId);
	        return addresses.stream()
	                .map(this::mapToDTO)
	                .collect(Collectors.toList());
	    }
	
	 private UserAddressDTO mapToDTO(UserAddress entity) {

		    return UserAddressDTO.builder()
		            .id(entity.getId())
		            .userId(
		                entity.getUser() != null 
		                    ? entity.getUser().getUserId() 
		                    : null
		            )
		            .addressLine1(entity.getAddressLine1())
		            .addressLine2(entity.getAddressLine2())
		            .city(entity.getCity())
		            .state(entity.getState())
		            .country(entity.getCountry())
		            .zipCode(entity.getZipCode())
		            .isDefault(entity.getDefaultAddress())
		            .addressType(entity.getAddressType())
		            .currentlyResidingFrom(entity.getCurrentlyResidingFrom())
		            .currentlyResidingAtThisAddress(
		                entity.getCurrentlyResidingAtThisAddress()
		            )
		            .isMyPermanentAddress(entity.getIsMyPermanentAddress())
		           // .verified(entity.isVerified())
		          //  .verificationStatus(entity.getVerificationStatus())
		            .build();
		}


	 
	 private UserAddress mapToEntity(UserAddressDTO dto, User user) {

		    UserAddress userAddress = UserAddress.builder()
		            .user(user)
		            .addressLine1(dto.getAddressLine1())
		            .addressLine2(dto.getAddressLine2())
		            .city(dto.getCity())
		            .state(dto.getState())
		            .country(dto.getCountry())
		            .zipCode(dto.getZipCode())
		            .defaultAddress(Boolean.TRUE.equals(dto.getIsDefault()))
		            .addressType(dto.getAddressType())
		            .currentlyResidingFrom(dto.getCurrentlyResidingFrom())
		            .currentlyResidingAtThisAddress(
		                    Boolean.TRUE.equals(dto.getCurrentlyResidingAtThisAddress())
		            )
		            .isMyPermanentAddress(
		                    Boolean.TRUE.equals(dto.getIsMyPermanentAddress())
		            )
		            .status("active")
		            .build();

		    log.info("UserAddress mapped from DTO: {}", dto);
		    return userAddress;
		}

	 
	 @Transactional
	 public List<UserAddressDTO> updateUserAddresses(
	         List<UserAddressDTO> addressDTOs,
	         Long userId
	 ) {
	     if (addressDTOs == null || addressDTOs.isEmpty()) {
	         throw new IllegalArgumentException("Address list cannot be empty");
	     }

	     userRepository.findById(userId)
	             .orElseThrow(() -> new RuntimeException("User not found"));

	     List<UserAddressDTO> result = new ArrayList();

	     for (UserAddressDTO dto : addressDTOs) {
	         result.add(updateUserAddress(dto, userId));
	     }

	     return result;
	 }
	 
	 @Transactional
	 public UserAddressDTO updateUserAddress(UserAddressDTO addressDTO, Long userId) {

	     User user = userRepository.findById(userId)
	             .orElseThrow(() -> new RuntimeException("User not found: " + userId));

	     UserAddress existingAddress;

	     // ================================
	     // CREATE or UPDATE
	     // ================================
	     if (addressDTO.getId() == null || addressDTO.getId() == 0) {
	         existingAddress = new UserAddress();
	         existingAddress.setUser(user);
	     } else {
	         existingAddress = useraddressRepository
	                 .findByIdAndUser_UserId(addressDTO.getId(), userId)
	                 .orElseThrow(() ->
	                         new RuntimeException("Address not found for user"));
	     }

	     // ================================
	     // MAP USER-EDITABLE FIELDS
	     // ================================
	     existingAddress.setAddressLine1(addressDTO.getAddressLine1());
	     existingAddress.setAddressLine2(addressDTO.getAddressLine2());
	     existingAddress.setCity(addressDTO.getCity());
	     existingAddress.setState(addressDTO.getState());
	     existingAddress.setCountry(addressDTO.getCountry());
	     existingAddress.setZipCode(addressDTO.getZipCode());

	     existingAddress.setAddressType(addressDTO.getAddressType());
	     existingAddress.setCurrentlyResidingFrom(addressDTO.getCurrentlyResidingFrom());
	     existingAddress.setCurrentlyResidingAtThisAddress(
	             Boolean.TRUE.equals(addressDTO.getCurrentlyResidingAtThisAddress())
	     );

	     existingAddress.setIsMyPermanentAddress(
	             Boolean.TRUE.equals(addressDTO.getIsMyPermanentAddress())
	     );

	     existingAddress.setDefaultAddress(
	             Boolean.TRUE.equals(addressDTO.getIsDefault())
	     );

	     // ================================
	     // ENSURE SINGLE DEFAULT ADDRESS
	     // ================================
	     if (Boolean.TRUE.equals(addressDTO.getIsDefault())) {
	         List<UserAddress> userAddresses =
	        		 useraddressRepository.findByUser_UserId(userId);

	         userAddresses.forEach(addr -> {
	             if (!addr.getId().equals(existingAddress.getId())) {
	                 addr.setDefaultAddress(false);
	             }
	         });

	         useraddressRepository.saveAll(userAddresses);
	     }

	     UserAddress saved = useraddressRepository.save(existingAddress);
	     return mapToDTO(saved);
	 }
	 public void deleteAddress(Long id, Long userId) {
	        UserAddress address = useraddressRepository.findByIdAndUser_UserId(id,userId)
	                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));
	        
	        useraddressRepository.delete(address);
	    }

	 
}
