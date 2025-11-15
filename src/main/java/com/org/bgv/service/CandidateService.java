package com.org.bgv.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.org.bgv.common.CandidateDTO;
import com.org.bgv.common.CommonUtils;
import com.org.bgv.common.ConsentRequest;
import com.org.bgv.common.ConsentResponse;
import com.org.bgv.common.RoleConstants;
import com.org.bgv.constants.Constants;
import com.org.bgv.controller.CompanyController;
import com.org.bgv.entity.Candidate;
import com.org.bgv.entity.CandidateConsent;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.CompanyUser;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.repository.CandidateConsentRepository;
import com.org.bgv.repository.CandidateRepository;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.CompanyUserRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.RoleRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.UserRoleRepository;
import com.org.bgv.s3.S3StorageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CandidateService {
	
	private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyUserRepository companyUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final CandidateRepository candidateRepository;
    private final CandidateConsentRepository consentRepository;
    private final S3StorageService s3StorageService;
    private final EmailService emailService;
    
    private static final Logger log = LoggerFactory.getLogger(CandidateService.class);
    
	public Boolean addCandidate(CandidateDTO candidateDTO) {

		// source type - university,individual,company
		try {
			String tempPassword = CommonUtils.generateTempPassword();
			User user = User.builder().firstName(candidateDTO.getFirstName()).lastName(candidateDTO.getLastName())
					.phoneNumber(candidateDTO.getMobileNo()).email(candidateDTO.getEmail())
					.gender(candidateDTO.getGender())
					// .password(UUID.randomUUID().toString())
					.password(passwordEncoder.encode(tempPassword))
					.userType(Constants.USER_TYPE_CANDIDATE)
					.status(Constants.USER_STATUS_ACTIVE)
					.passwordResetrequired(Boolean.TRUE)
					.build();

			// Save user first (if User is a new entity)
			userRepository.save(user);

			// Find Role
			Role companyRole = roleRepository.findByName(RoleConstants.ROLE_CANDIDATE)
					.orElseThrow(() -> new RuntimeException(RoleConstants.ROLE_CANDIDATE + " not found"));

			// Create UserRole mapping
			UserRole userRole = UserRole.builder().user(user).role(companyRole).build();

			userRoleRepository.save(userRole);

			// Find Company
			Company company = companyRepository.findById(candidateDTO.getCompanyId()).orElseThrow(
					() -> new RuntimeException("Company not found with ID: " + candidateDTO.getCompanyId()));

			// Create CompanyUser mapping
			CompanyUser companyUser = new CompanyUser();
			companyUser.setCompany(company);
			companyUser.setUser(user);

			companyUserRepository.save(companyUser);
			
			

	        Profile profile =  Profile.builder()
            //  .profileId(dto.getBasicDetails().getProfileId())
              .firstName(candidateDTO.getFirstName())
              .lastName(candidateDTO.getLastName())
              .emailAddress(candidateDTO.getEmail())
              .phoneNumber(candidateDTO.getMobileNo())
             // .dateOfBirth(employeeDTO.getDateOfBirth())
              .gender(candidateDTO.getGender())
             // .userId(dto.getUser_id())
              .user(user)
              .status(Constants.CANDIDATE_STATUS_CREATED)
              .build();
			
	        profileRepository.save(profile);
	        
	        Candidate candidate = Candidate.builder()
	        .company(company)
	        .createdAt(LocalDateTime.now())
	        .isActive(Boolean.TRUE)
	        .isVerified(Boolean.FALSE)
	        .sourceType(Constants.CANDIDATE_SOURCE_EMPLOYER)
	        .verificationStatus(Constants.CANDIDATE_STATUS_CREATED)
	        .user(user)
	        .isConsentProvided(Boolean.FALSE)
	        .build();  
	        
	        candidateRepository.save(candidate);
	        
	        
	        // Send an Email - Account creation
	        
	        emailService.sendEmailToEmployeeRegistrationSuccess(user,tempPassword);
			
			return Boolean.TRUE;
		} catch (Exception e) {
			log.error("Exception while creating Candidate::::::{}", e.getMessage());
		}
		return Boolean.FALSE;
	}
	
	public ConsentResponse saveConsent(ConsentRequest consentRequest) {
	    try {
	        log.info("Received consent request - candidateId: {}, consentType: {}", 
	                 consentRequest.getCandidateId(), consentRequest.getConsentType());
	        
	        CandidateConsent.ConsentType consentType = CandidateConsent.ConsentType.valueOf(
	            consentRequest.getConsentType().toUpperCase()
	        );

	        CandidateConsent.CandidateConsentBuilder consentBuilder = CandidateConsent.builder()
	                .candidateId(consentRequest.getCandidateId())
	                .consentType(consentType)
	                .policyVersion(consentRequest.getPolicyVersion())
	                .ipAddress(consentRequest.getIpAddress())
	                .userAgent(consentRequest.getUserAgent())
	                .consentedAt(java.time.LocalDateTime.now());

	        // Handle signature data - remove quotes if present
	        if (consentRequest.getSignatureData() != null && !consentRequest.getSignatureData().isEmpty()) {
	            String signatureData = consentRequest.getSignatureData();
	            
	            // Remove surrounding quotes if present
	            if (signatureData.startsWith("\"") && signatureData.endsWith("\"")) {
	                signatureData = signatureData.substring(1, signatureData.length() - 1);
	                log.info("Removed quotes from signature data");
	            }
	            
	            // Check if it's a base64 image data URL
	            if (signatureData.startsWith("data:image/")) {
	                log.info("Processing base64 image signature data");
	                
	                try {
	                    // Extract base64 data
	                    String[] parts = signatureData.split(",");
	                    String imageData = parts[1];
	                    String mimeType = parts[0].split(":")[1].split(";")[0];
	                    
	                    log.info("MIME type: {}, Data length: {}", mimeType, imageData.length());
	                    
	                    // Convert base64 to MultipartFile for S3 upload
	                    MultipartFile signatureImage = base64ToMultipartFile(imageData, mimeType, "signature.png");
	                    
	                    // Upload signature image to S3
	                    Pair<String, String> uploadResult = s3StorageService.uploadFile(
	                        signatureImage, 
	                        "signatures"
	                    );
	                    
	                    consentBuilder.signatureUrl(uploadResult.getFirst());
	                    consentBuilder.signatureS3Key(uploadResult.getSecond());
	                    
	                    log.info("Signature image uploaded to S3 for candidate: {}, URL: {}", 
	                             consentRequest.getCandidateId(), uploadResult.getFirst());
	                    
	                } catch (Exception e) {
	                    log.error("Error processing base64 signature data: {}", e.getMessage());
	                    // Don't store anything if processing fails
	                }
	            } else {
	                log.warn("Signature data doesn't start with 'data:image/' after quote removal");
	                log.info("Data starts with: '{}'", 
	                    signatureData.substring(0, Math.min(20, signatureData.length())));
	            }
	        }

	        // Handle file upload to S3
	        if (consentRequest.getConsentFile() != null && !consentRequest.getConsentFile().isEmpty()) {
	            Pair<String, String> uploadResult = s3StorageService.uploadFile(
	                consentRequest.getConsentFile(), 
	                "consents"
	            );
	            
	            consentBuilder.documentUrl(uploadResult.getFirst());
	            consentBuilder.documentS3Key(uploadResult.getSecond());
	            consentBuilder.originalFileName(consentRequest.getConsentFile().getOriginalFilename());
	            consentBuilder.fileType(consentRequest.getConsentFile().getContentType());
	            consentBuilder.fileSize(consentRequest.getConsentFile().getSize());
	            
	            log.info("Document uploaded to S3 for candidate: {}, URL: {}", 
	                     consentRequest.getCandidateId(), uploadResult.getFirst());
	        }

	        CandidateConsent savedConsent = consentRepository.save(consentBuilder.build());
	        
	        log.info("consent saved successfully::::::::{}",consentRequest.getCandidateId());
	        
	        // Update candidate's consent status
	        updateCandidateConsentStatus(consentRequest.getCandidateId());
	        
	        return mapToResponse(savedConsent, "Consent saved successfully");

	    } catch (IllegalArgumentException e) {
	        throw new RuntimeException("Invalid consent type: " + consentRequest.getConsentType());
	    } catch (Exception e) {
	        log.error("Error saving consent for candidate: {}", consentRequest.getCandidateId(), e);
	        throw new RuntimeException("Error saving consent: " + e.getMessage());
	    }
	}
	public static MultipartFile base64ToMultipartFile(String base64Data, String mimeType, String fileName) {
        try {
            // Remove data URL prefix if present
            if (base64Data.contains(",")) {
                base64Data = base64Data.split(",")[1];
            }
            
            log.info("base64Data::::::::::::::::::::{}",base64Data);

            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

            // Return anonymous implementation of MultipartFile
            return new MultipartFile() {
                @Override
                public String getName() {
                    return fileName;
                }

                @Override
                public String getOriginalFilename() {
                    return fileName;
                }

                @Override
                public String getContentType() {
                    return mimeType;
                }

                @Override
                public boolean isEmpty() {
                    return decodedBytes.length == 0;
                }

                @Override
                public long getSize() {
                    return decodedBytes.length;
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return decodedBytes;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return new ByteArrayInputStream(decodedBytes);
                }

                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {
                    try (FileOutputStream fos = new FileOutputStream(dest)) {
                        fos.write(decodedBytes);
                    }
                }
            };

        } catch (Exception e) {
        	log.error("error in base64ToMultipartFile:::::"+e.getMessage());
            throw new RuntimeException("Error converting base64 to MultipartFile", e);
        }
    }
    public List<ConsentResponse> getConsentsByCandidateId(Long candidateId) {
        return consentRepository.findByCandidateId(candidateId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ConsentResponse> getConsentsByCandidateIdAndType(Long candidateId, String consentType) {
        CandidateConsent.ConsentType type = CandidateConsent.ConsentType.valueOf(consentType.toUpperCase());
        return consentRepository.findByCandidateIdAndConsentType(candidateId, type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    

    public void deleteConsent(Long consentId) {
        CandidateConsent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new RuntimeException("Consent not found with id: " + consentId));
        
        // Delete file from S3 if exists
        if (consent.getDocumentS3Key() != null) {
            try {
                s3StorageService.deleteFile(consent.getDocumentS3Key());
                log.info("Deleted file from S3 with key: {}", consent.getDocumentS3Key());
            } catch (Exception e) {
                log.warn("Failed to delete file from S3 with key: {}", consent.getDocumentS3Key(), e);
            }
        }
        
        consentRepository.delete(consent);
        log.info("Deleted consent record with id: {}", consentId);
    }
    
    public boolean hasCandidateProvidedConsent(Long candidateId) {
        try {
            Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
            
            if (candidateOpt.isEmpty()) {
                log.warn("Candidate not found with ID: {}", candidateId);
                return false;
            }
            
            Candidate candidate = candidateOpt.get();
            Boolean isConsentProvided = candidate.getIsConsentProvided();
            
            // Return true only if isConsentProvided is explicitly true
            boolean hasConsent = Boolean.TRUE.equals(isConsentProvided);
            
            log.debug("Consent check for candidate {}: isConsentProvided = {}", candidateId, hasConsent);
            return hasConsent;
            
        } catch (Exception e) {
            log.error("Error checking consent for candidate: {}", candidateId, e);
            return false;
        }
    }
    
    public Candidate getCandidateByUserId(Long userId) {
        try {
            Optional<Candidate> candidateOpt = candidateRepository.findByUserUserId(userId);
            
            if (candidateOpt.isEmpty()) {
                log.warn("Candidate not found for user ID: {}", userId);
                return null; // Return null instead of throwing exception
            }
            
            Candidate candidate = candidateOpt.get();
            log.debug("Found candidate: {} for user ID: {}", candidate.getCandidateId(), userId);
            return candidate;
            
        } catch (Exception e) {
            log.error("Error fetching candidate for user ID: {}", userId, e);
            return null; // Return null in case of any exception
        }
    }


    // Additional method to upload signature as image
    public String uploadSignatureImage(MultipartFile signatureImage) {
        Pair<String, String> uploadResult = s3StorageService.uploadFile(signatureImage, "signatures");
        log.info("Signature image uploaded to S3: {}", uploadResult.getFirst());
        return uploadResult.getFirst();
    }
    
    public ConsentResponse getConsentById(Long consentId) {
        CandidateConsent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new RuntimeException("Consent not found with id: " + consentId));
        return mapToResponse(consent);
    }

    private ConsentResponse mapToResponse(CandidateConsent consent) {
        return mapToResponse(consent, null);
    }

    private ConsentResponse mapToResponse(CandidateConsent consent, String message) {
        ConsentResponse response = new ConsentResponse();
        response.setId(consent.getId());
        response.setCandidateId(consent.getCandidateId());
        response.setConsentType(consent.getConsentType().toString());
        response.setSignatureUrl(consent.getSignatureUrl());
        response.setDocumentUrl(consent.getDocumentUrl());
        response.setOriginalFileName(consent.getOriginalFileName());
        response.setFileType(consent.getFileType());
        response.setFileSize(consent.getFileSize());
        response.setConsentedAt(consent.getConsentedAt());
        response.setMessage(message);
        return response;
    }
	
    /**
     * Update candidate's consent status based on their consent records
     */
    @Transactional
    public void updateCandidateConsentStatus(Long candidateId) {
        try {
        	log.info("updateCandidateConsentStatus::::::method:::::START::::::candidateId::{}",candidateId);
            Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
            
            if (candidateOpt.isPresent()) {
            	log.info("updateCandidateConsentStatus::::::method:::::START::::::candidateOpt.isPresent()::{}",candidateOpt.isPresent());
            	
                Candidate candidate = candidateOpt.get();
                
                candidate.setIsConsentProvided(Boolean.TRUE);
                candidate = candidateRepository.save(candidate);
                
                log.info("updateCandidateConsentStatus::::::{}",candidate);
               
            } else {
                log.warn("Candidate not found with ID: {}", candidateId);
            }
        } catch (Exception e) {
            log.error("Error updating consent status for candidate: {}", candidateId, e);
            throw new RuntimeException("Failed to update candidate consent status: " + e.getMessage());
        }
    }

}
