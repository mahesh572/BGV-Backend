package com.org.bgv.service;

import com.org.bgv.dto.ProfileAddressDTO;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.Profile_Address;
import com.org.bgv.repository.ProfileAddressRepository;
import com.org.bgv.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileAddressService {

    private final ProfileAddressRepository profileAddressRepository;
    private final ProfileRepository profileRepository;

    public ProfileAddressDTO createProfileAddress(ProfileAddressDTO profileAddressDTO, Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));
        
        Profile_Address profileAddress = mapToEntity(profileAddressDTO, profile);
        Profile_Address savedAddress = profileAddressRepository.save(profileAddress);
        return mapToDTO(savedAddress);
    }

    public List<ProfileAddressDTO> getAddressesByProfile(Long profileId) {
        List<Profile_Address> addresses = profileAddressRepository.findByProfile_ProfileId(profileId);
        return addresses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProfileAddressDTO getProfileAddressById(Long id) {
        Profile_Address profileAddress = profileAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile address not found with id: " + id));
        return mapToDTO(profileAddress);
    }

    public List<ProfileAddressDTO> getAllProfileAddresses() {
        List<Profile_Address> addresses = profileAddressRepository.findAll();
        return addresses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProfileAddressDTO updateProfileAddress(ProfileAddressDTO profileAddressDTO,Long profileId) {
    	Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));
    	Profile_Address existingAddress = null;
    	if(profileAddressDTO.getId()==null || profileAddressDTO.getId()==0) {
    		existingAddress = new Profile_Address();
    	}else {
         existingAddress = profileAddressRepository.findById(profileAddressDTO.getId())
                .orElseThrow(() -> new RuntimeException("Profile address not found with id: " + profileAddressDTO.getId()));
    	}
    	existingAddress.setProfile(profile);
        existingAddress.setAddress_line1(profileAddressDTO.getAddressLine1());
        existingAddress.setCity(profileAddressDTO.getCity());
        existingAddress.setState(profileAddressDTO.getState());
        existingAddress.setCountry(profileAddressDTO.getCountry());
        existingAddress.setZip_code(profileAddressDTO.getZipCode());
        existingAddress.setCur_residing(profileAddressDTO.isCurrentlyResidingAtThisAddress());
        existingAddress.setCur_residing_from(profileAddressDTO.getCurrentlyResidingFrom());
        existingAddress.setIs_permenet_address(profileAddressDTO.getIsMyPermanentAddress());

        Profile_Address updatedAddress = profileAddressRepository.save(existingAddress);
        return mapToDTO(updatedAddress);
    }
    
    @Transactional
    public List<ProfileAddressDTO> updateProfileAddresses(List<ProfileAddressDTO> addressDTOs, Long profileId) {
    	
    	List<ProfileAddressDTO> addressList = new ArrayList<>();
    	
    	for(ProfileAddressDTO addressDTO :addressDTOs) {
    		
    		ProfileAddressDTO profileAddressDTO = updateProfileAddress(addressDTO,profileId);
    		addressList.add(profileAddressDTO);
    	}
    	return addressList;
    }
    public void deleteProfileAddress(Long id,Long profileId) {
        Profile_Address profileAddress = profileAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile address not found with id: " + id));
        profileAddressRepository.delete(profileAddress);
    }

    @Transactional
    public List<ProfileAddressDTO> saveProfileAddresses(List<ProfileAddressDTO> profileAddressDTOs, Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));

        List<Profile_Address> addresses = profileAddressDTOs.stream()
                .map(dto -> mapToEntity(dto, profile))
                .collect(Collectors.toList());

        List<Profile_Address> savedAddresses = profileAddressRepository.saveAll(addresses);
        return savedAddresses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private Profile_Address mapToEntity(ProfileAddressDTO dto, Profile profile) {
        return Profile_Address.builder()
               // .profile_address_id(dto.getProfileAddressId())
                .profile(profile)
                .address_line1(dto.getAddressLine1())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .zip_code(dto.getZipCode())
                .cur_residing(dto.isCurrentlyResidingAtThisAddress())
                .cur_residing_from(dto.getCurrentlyResidingFrom())
                .is_permenet_address(dto.getIsMyPermanentAddress())
                .build();
    }

    private ProfileAddressDTO mapToDTO(Profile_Address entity) {
        return ProfileAddressDTO.builder()
                .id(entity.getProfile_address_id())
               // .profile_id(entity.getProfile() != null ? entity.getProfile().getProfileId() : null)
                .addressLine1(entity.getAddress_line1())
                .city(entity.getCity())
                .state(entity.getState())
                .country(entity.getCountry())
                .zipCode(entity.getZip_code())
                .currentlyResidingAtThisAddress(entity.getCur_residing())
                .currentlyResidingFrom(entity.getCur_residing_from())
                .isMyPermanentAddress(entity.getIs_permenet_address())
                .build();
    }
    public void deleteProfileAddressByProfileId(Long profileId) {
    	profileAddressRepository.deleteByProfile_ProfileId(profileId);
    }
}