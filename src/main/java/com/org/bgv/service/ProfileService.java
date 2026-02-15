package com.org.bgv.service;

import com.org.bgv.dto.ProfileDTO;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.User;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.s3.S3StorageService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final S3StorageService s3StorageService;

    /* =====================================================
       CREATE
       ===================================================== */
    public ProfileDTO createProfile(ProfileDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Prevent duplicate profile per user
        profileRepository.findByUser_UserId(user.getUserId())
                .ifPresent(p -> {
                    throw new IllegalStateException("Profile already exists for user");
                });

        Profile profile = mapToEntity(dto, user);

        Profile savedProfile = profileRepository.save(profile);
        return mapToDTO(savedProfile);
    }

    /* =====================================================
       READ
       ===================================================== */
    public ProfileDTO getProfileById(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
        return mapToDTO(profile);
    }

    public ProfileDTO getProfileByUserId(Long userId) {
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found for user"));
        return mapToDTO(profile);
    }

    public List<ProfileDTO> getAllProfiles() {
        return profileRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /* =====================================================
       UPDATE
       ===================================================== */
    @Transactional
    public ProfileDTO updateProfile(Long profileId, ProfileDTO dto) {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
        
        logger.info("updateProfile::::::::::::::::::::::{}",dto);

        updateEntity(profile, dto);

        return mapToDTO(profile);
    }

    /* =====================================================
       DELETE
       ===================================================== */
    @Transactional
    public void deleteProfile(Long profileId) {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

        profileRepository.delete(profile);
    }

    /* =====================================================
       STATUS UPDATE
       ===================================================== */
    @Transactional
    public void updateProfileStatus(Long profileId, String newStatus) {

        logger.info("Updating profile {} to status {}", profileId, newStatus);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

        profile.setStatus(newStatus);
    }

    /* =====================================================
       UTILITY
       ===================================================== */
    public Long getProfileIdByUserId(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        return profileRepository.findProfileIdByUserId(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Profile not found for user ID: " + userId));
    }

    /* =====================================================
       MAPPERS
       ===================================================== */

    private Profile mapToEntity(ProfileDTO dto, User user) {

        return Profile.builder()
                .user(user)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .nationality(dto.getNationality())
                .gender(dto.getGender())
                .dateOfBirth(dto.getDateOfBirth())
                .maritalStatus(dto.getMaritalStatus())
                .phoneNumber(dto.getPhoneNumber())
               // .passportNumber(dto.getPassportNumber())
               // .passportExpiry(dto.getPassportExpiry())
                .linkedinUrl(dto.getLinkedinUrl())
                .consentProvided(dto.getConsentProvided())
                .consentSource(dto.getConsentSource())
                .status(dto.getStatus())
                .build();
    }

    private void updateEntity(Profile profile, ProfileDTO dto) {

        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setNationality(dto.getNationality());
        profile.setGender(dto.getGender());
        profile.setDateOfBirth(dto.getDateOfBirth());
        profile.setMaritalStatus(dto.getMaritalStatus());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setNamePrefix(dto.getNamePrefix());
        profile.setParentName(dto.getParentName());
        profile.setParentRelationship(dto.getParentRelationship());
      //  profile.setPassportNumber(dto.getPassportNumber());
      //  profile.setPassportExpiry(dto.getPassportExpiry());
        profile.setLinkedinUrl(dto.getLinkedinUrl());
        profile.setConsentProvided(dto.getConsentProvided());
        profile.setConsentSource(dto.getConsentSource());
        profile.setStatus(dto.getStatus());
    }

    private ProfileDTO mapToDTO(Profile profile) {

        return ProfileDTO.builder()
                .profileId(profile.getProfileId())
                .userId(profile.getUser().getUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .nationality(profile.getNationality())
                .gender(profile.getGender())
                .dateOfBirth(profile.getDateOfBirth())
                .maritalStatus(profile.getMaritalStatus())
                .phoneNumber(profile.getPhoneNumber())
                .namePrefix(profile.getNamePrefix())
                .parentName(profile.getParentName())
                .parentRelationship(profile.getParentRelationship())
                .phoneVerified(profile.getPhoneVerified())
                .profilePicUrl(profile.getProfilepicurl())
              //  .identityVerified(profile.getIdentityVerified())
              //  .passportNumber(profile.getPassportNumber())
             //   .passportExpiry(profile.getPassportExpiry())
                .linkedinUrl(profile.getLinkedinUrl())
                .consentProvided(profile.getConsentProvided())
                .consentSource(profile.getConsentSource())
                .status(profile.getStatus())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
    
    @Transactional
    public void uploadProfilePicture(Long userId, MultipartFile file) {

        Profile profile = profileRepository.findByUserUserId(userId);
                

        validateFile(file);

        // Delete old picture if exists
        if (profile.getProfilepicKey() != null) {
            s3StorageService.deleteFile(profile.getProfilepicKey());
        }

        String folderName = "profile/" + userId;

        // Upload file to S3
        Pair<String, String> upload = s3StorageService.uploadFile(file, folderName);

        // Set URL and Key in profile
        profile.setProfilepicurl(upload.getFirst());  // public URL
        profile.setProfilepicKey(upload.getSecond()); // S3 key/reference
        profileRepository.save(profile);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
    }


}
