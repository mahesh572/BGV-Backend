package com.org.bgv.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.org.bgv.candidate.CandidateMapper;
import com.org.bgv.candidate.CandidateSearchRequest;
import com.org.bgv.common.CandidateDTO;
import com.org.bgv.common.ColumnMetadata;
import com.org.bgv.common.CommonUtils;
import com.org.bgv.common.ConsentRequest;
import com.org.bgv.common.ConsentResponse;
import com.org.bgv.common.FilterMetadata;
import com.org.bgv.common.FilterRequest;
import com.org.bgv.common.Option;
import com.org.bgv.common.PaginationMetadata;
import com.org.bgv.common.PaginationRequest;
import com.org.bgv.common.PaginationResponse;
import com.org.bgv.common.RoleConstants;
import com.org.bgv.common.SortField;
import com.org.bgv.common.SortingMetadata;
import com.org.bgv.common.SortingRequest;
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

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
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
    private final CandidateMapper candidateMapper;
    
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
    /*
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
    */
    public CandidateDTO getCandidateByUserId(Long userId) {
        return candidateRepository.findByUserUserId(userId)
                .map(candidateMapper::toDto)
                .orElse(null);
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
    
    public CandidateDTO getCandidateById(Long companyId, Long candidateId) {

        return candidateRepository
                .findByCompanyIdAndCandidateId(companyId, candidateId)
                .map(candidateMapper::toDto)
                .orElseThrow(() -> new RuntimeException(
                        "Candidate not found for companyId: " + companyId + " and candidateId: " + candidateId
                ));
    }
    public List<CandidateDTO> getAllCandidates() {
        return candidateRepository.findAll()
                .stream()
                .map(candidateMapper::toDto)
                .collect(Collectors.toList());
    }
    

    public PaginationResponse<CandidateDTO> searchCandidates(CandidateSearchRequest searchRequest) {
        Pageable pageable = createPageable(searchRequest.getPagination(), searchRequest.getSorting());
        
        // Build specification for filtering
        Specification<Candidate> spec = buildSearchSpecification(searchRequest);
        
        Page<Candidate> candidatePage = candidateRepository.findAll(spec, pageable);
        
        return buildCompletePaginationResponse(candidatePage, searchRequest);
    }
    

    private Pageable createPageable(PaginationRequest pagination, SortingRequest sorting) {
        if (pagination == null) {
            pagination = PaginationRequest.builder()
                    .page(0)
                    .size(10)
                    .build();
        }
        
        if (sorting == null) {
            sorting = SortingRequest.builder()
                    .sortBy("createdAt")
                    .sortDirection("desc")
                    .build();
        }
        
        Sort sort = Sort.by(
            sorting.getSortDirection().equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC, 
            sorting.getSortBy()
        );
        return PageRequest.of(pagination.getPage(), pagination.getSize(), sort);
    }

    private Specification<Candidate> buildSearchSpecification(CandidateSearchRequest searchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filter by company if specified
            if (searchRequest.getCompanyId() != null && searchRequest.getCompanyId() != 0) {
                Join<Candidate, Company> companyJoin = root.join("company", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(companyJoin.get("id"), searchRequest.getCompanyId()));
            }
            
            // Search term across user fields and candidate fields
            if (StringUtils.hasText(searchRequest.getSearch())) {
                String searchTerm = "%" + searchRequest.getSearch().toLowerCase() + "%";
                
                // Join with User entity for user fields
                Join<Candidate, User> userJoin = root.join("user", JoinType.INNER);
                
                Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("firstName")), searchTerm);
                Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("lastName")), searchTerm);
                Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("email")), searchTerm);
                Predicate phonePredicate = criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("phoneNumber")), searchTerm);
                
                // Candidate specific fields
                Predicate sourceTypePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("sourceType")), searchTerm);
                Predicate verificationStatusPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("verificationStatus")), searchTerm);
                Predicate jobSearchStatusPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("jobSearchStatus")), searchTerm);
                
                predicates.add(criteriaBuilder.or(
                    firstNamePredicate, lastNamePredicate, emailPredicate, phonePredicate,
                    sourceTypePredicate, verificationStatusPredicate, jobSearchStatusPredicate
                ));
            }
            
            // Filters
            if (searchRequest.getFilters() != null) {
                for (FilterRequest filter : searchRequest.getFilters()) {
                    if (filter.getIsSelected() != null && filter.getIsSelected() && filter.getSelectedValue() != null) {
                        switch (filter.getField()) {
                            case "isActive":
                                Boolean activeValue = Boolean.valueOf(filter.getSelectedValue().toString());
                                predicates.add(criteriaBuilder.equal(root.get("isActive"), activeValue));
                                break;
                            case "isVerified":
                                Boolean verifiedValue = Boolean.valueOf(filter.getSelectedValue().toString());
                                predicates.add(criteriaBuilder.equal(root.get("isVerified"), verifiedValue));
                                break;
                            case "verificationStatus":
                                predicates.add(criteriaBuilder.equal(root.get("verificationStatus"), filter.getSelectedValue()));
                                break;
                            case "jobSearchStatus":
                                predicates.add(criteriaBuilder.equal(root.get("jobSearchStatus"), filter.getSelectedValue()));
                                break;
                            case "isConsentProvided":
                                Boolean consentValue = Boolean.valueOf(filter.getSelectedValue().toString());
                                predicates.add(criteriaBuilder.equal(root.get("isConsentProvided"), consentValue));
                                break;
                            case "sourceType":
                                predicates.add(criteriaBuilder.equal(root.get("sourceType"), filter.getSelectedValue()));
                                break;
                            case "createdDate":
                                // Handle date range - you might want to implement proper date range logic
                                // For now, exact match
                                try {
                                    LocalDateTime dateValue = LocalDateTime.parse(filter.getSelectedValue().toString());
                                    predicates.add(criteriaBuilder.equal(root.get("createdAt"), dateValue));
                                } catch (Exception e) {
                                    log.warn("Invalid date format for createdDate filter: {}", filter.getSelectedValue());
                                }
                                break;
                        }
                    }
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private PaginationResponse<CandidateDTO> buildCompletePaginationResponse(Page<Candidate> candidatePage, CandidateSearchRequest searchRequest) {
        List<CandidateDTO> candidateDtos = candidatePage.getContent()
                .stream()
                .map(candidateMapper::toDto) // Using normal mapper
                .collect(Collectors.toList());

        // Build pagination metadata
        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(candidatePage.getNumber())
                .pageSize(candidatePage.getSize())
                .totalElements(candidatePage.getTotalElements())
                .totalPages(candidatePage.getTotalPages())
                .hasNext(candidatePage.hasNext())
                .hasPrevious(candidatePage.hasPrevious())
                .allowedPageSizes(Arrays.asList(10, 25, 50, 100))
                .build();

        // Build sorting metadata
        SortingMetadata sortingMetadata = SortingMetadata.builder()
                .currentSort(SortField.builder()
                        .field(searchRequest.getSorting().getSortBy())
                        .displayName(getDisplayNameForField(searchRequest.getSorting().getSortBy()))
                        .direction(searchRequest.getSorting().getSortDirection())
                        .build())
                .sortableFields(getSortableFields())
                .build();

        // Build filters metadata with selected values
        List<FilterMetadata> filters = getAvailableFilters(searchRequest);

        // Build columns metadata
        List<ColumnMetadata> columns = getColumnMetadata(searchRequest.getCompanyId());

        return PaginationResponse.<CandidateDTO>builder()
                .content(candidateDtos)
                .pagination(paginationMetadata)
                .sorting(sortingMetadata)
                .filters(filters)
                .columns(columns)
                .build();
    }

    private List<SortField> getSortableFields() {
        return Arrays.asList(
            SortField.builder().field("candidateId").displayName("Candidate ID").build(),
            SortField.builder().field("user.firstName").displayName("First Name").build(),
            SortField.builder().field("user.lastName").displayName("Last Name").build(),
            SortField.builder().field("user.email").displayName("Email").build(),
            SortField.builder().field("createdAt").displayName("Created Date").build(),
            SortField.builder().field("updatedAt").displayName("Updated Date").build(),
            SortField.builder().field("verificationStatus").displayName("Verification Status").build(),
            SortField.builder().field("jobSearchStatus").displayName("Job Search Status").build(),
            SortField.builder().field("sourceType").displayName("Source Type").build()
        );
    }

    private List<FilterMetadata> getAvailableFilters(CandidateSearchRequest searchRequest) {
        List<FilterMetadata> filters = new ArrayList<>();
        
        // Verification Status filter
        FilterMetadata verificationFilter = FilterMetadata.builder()
                .field("verificationStatus")
                .displayName("Verification Status")
                .type("dropdown")
                .options(Arrays.asList(
                    Option.builder().label("Pending").value("PENDING").build(),
                    Option.builder().label("Verified").value("VERIFIED").build(),
                    Option.builder().label("Rejected").value("REJECTED").build()
                ))
                .build();

        // Job Search Status filter
        FilterMetadata jobSearchFilter = FilterMetadata.builder()
                .field("jobSearchStatus")
                .displayName("Job Search Status")
                .type("dropdown")
                .options(Arrays.asList(
                    Option.builder().label("Active").value("ACTIVE").build(),
                    Option.builder().label("Passive").value("PASSIVE").build(),
                    Option.builder().label("Not Looking").value("NOT_LOOKING").build()
                ))
                .build();

        // Active Status filter
        FilterMetadata activeFilter = FilterMetadata.builder()
                .field("isActive")
                .displayName("Active Status")
                .type("dropdown")
                .options(Arrays.asList(
                    Option.builder().label("Active").value("true").build(),
                    Option.builder().label("Inactive").value("false").build()
                ))
                .build();

        // Verified Status filter
        FilterMetadata verifiedFilter = FilterMetadata.builder()
                .field("isVerified")
                .displayName("Verified")
                .type("dropdown")
                .options(Arrays.asList(
                    Option.builder().label("Verified").value("true").build(),
                    Option.builder().label("Not Verified").value("false").build()
                ))
                .build();

        // Consent Provided filter
        FilterMetadata consentFilter = FilterMetadata.builder()
                .field("isConsentProvided")
                .displayName("Consent Provided")
                .type("dropdown")
                .options(Arrays.asList(
                    Option.builder().label("Yes").value("true").build(),
                    Option.builder().label("No").value("false").build()
                ))
                .build();

        // Source Type filter
        FilterMetadata sourceTypeFilter = FilterMetadata.builder()
                .field("sourceType")
                .displayName("Source Type")
                .type("dropdown")
                .options(Arrays.asList(
                    Option.builder().label("Portal").value("PORTAL").build(),
                    Option.builder().label("Referral").value("REFERRAL").build(),
                    Option.builder().label("Agency").value("AGENCY").build(),
                    Option.builder().label("Other").value("OTHER").build()
                ))
                .build();

        // Date range filter
        FilterMetadata dateFilter = FilterMetadata.builder()
                .field("createdDate")
                .displayName("Created Date Range")
                .type("dateRange")
                .build();

        // Set selected values if any
        if (searchRequest.getFilters() != null) {
            for (FilterRequest filter : searchRequest.getFilters()) {
                if (filter.getIsSelected() != null && filter.getIsSelected()) {
                    switch (filter.getField()) {
                        case "verificationStatus":
                            verificationFilter.setSelectedValue(filter.getSelectedValue());
                            break;
                        case "jobSearchStatus":
                            jobSearchFilter.setSelectedValue(filter.getSelectedValue());
                            break;
                        case "isActive":
                            activeFilter.setSelectedValue(filter.getSelectedValue());
                            break;
                        case "isVerified":
                            verifiedFilter.setSelectedValue(filter.getSelectedValue());
                            break;
                        case "isConsentProvided":
                            consentFilter.setSelectedValue(filter.getSelectedValue());
                            break;
                        case "sourceType":
                            sourceTypeFilter.setSelectedValue(filter.getSelectedValue());
                            break;
                        case "createdDate":
                            dateFilter.setSelectedValue(filter.getSelectedValue());
                            break;
                    }
                }
            }
        }

        filters.add(verificationFilter);
        filters.add(jobSearchFilter);
        filters.add(activeFilter);
        filters.add(verifiedFilter);
        filters.add(consentFilter);
        filters.add(sourceTypeFilter);
        filters.add(dateFilter);

        return filters;
    }

    private List<ColumnMetadata> getColumnMetadata(Long companyId) {
        List<ColumnMetadata> columns = new ArrayList();
        
        columns.add(ColumnMetadata.builder().field("candidateId").displayName("Candidate ID").visible(false).build());
        columns.add(ColumnMetadata.builder().field("name").displayName("Name").visible(true).build());
        columns.add(ColumnMetadata.builder().field("email").displayName("Email").visible(true).build());
        columns.add(ColumnMetadata.builder().field("phoneNumber").displayName("Phone").visible(true).build());
        columns.add(ColumnMetadata.builder().field("sourceType").displayName("Source Type").visible(false).build());
        columns.add(ColumnMetadata.builder().field("verificationStatus").displayName("Verification Status").visible(true).build());
        columns.add(ColumnMetadata.builder().field("isActive").displayName("Active").visible(false).build());
        columns.add(ColumnMetadata.builder().field("isVerified").displayName("Verified").visible(false).build());
        columns.add(ColumnMetadata.builder().field("jobSearchStatus").displayName("Job Search Status").visible(false).build());
        columns.add(ColumnMetadata.builder().field("isConsentProvided").displayName("Consent Provided").visible(false).build());
        columns.add(ColumnMetadata.builder().field("createdAt").displayName("Created Date").visible(false).build());
        
        if (companyId == null || companyId == 0) {
            columns.add(ColumnMetadata.builder().field("companyName").displayName("Company").visible(false).build());
        }
        
        return columns;
    }

    private String getDisplayNameForField(String field) {
        switch (field) {
            case "candidateId": return "Candidate ID";
            case "user.firstName": return "First Name";
            case "user.lastName": return "Last Name";
            case "user.email": return "Email";
            case "createdAt": return "Created Date";
            case "updatedAt": return "Updated Date";
            case "verificationStatus": return "Verification Status";
            case "jobSearchStatus": return "Job Search Status";
            case "sourceType": return "Source Type";
            default: return field;
        }
    }

   /*
    // CRUD operations
    public CandidateDto createCandidate(CandidateDto candidateDto) {
        // Validate required fields
        if (candidateDto.getUserId() == null) {
            throw new RuntimeException("User ID is required");
        }
        if (candidateDto.getCompanyId() == null) {
            throw new RuntimeException("Company ID is required");
        }
        
        // Check if candidate already exists for user
        if (candidateRepository.existsByUserUserId(candidateDto.getUserId())) {
            throw new RuntimeException("Candidate already exists for this user");
        }
        
        // Get user and company
        User user = userRepository.findById(candidateDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + candidateDto.getUserId()));
        
        Company company = companyRepository.findById(candidateDto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + candidateDto.getCompanyId()));
        
        // Create candidate
        Candidate candidate = Candidate.builder()
                .user(user)
                .company(company)
                .sourceType(candidateDto.getSourceType())
                .jobSearchStatus(candidateDto.getJobSearchStatus())
                .isConsentProvided(candidateDto.getIsConsentProvided())
                .build();
        
        Candidate savedCandidate = candidateRepository.save(candidate);
        return candidateMapper.toDto(savedCandidate); // Using normal mapper
    }
*/
    public CandidateDTO updateCandidate(Long candidateId, CandidateDTO candidateDto) {
        Candidate existingCandidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + candidateId));
        
        // Update fields
        if (candidateDto.getSourceType() != null) {
            existingCandidate.setSourceType(candidateDto.getSourceType());
        }
        if (candidateDto.getJobSearchStatus() != null) {
            existingCandidate.setJobSearchStatus(candidateDto.getJobSearchStatus());
        }
        if (candidateDto.getIsActive() != null) {
            existingCandidate.setIsActive(candidateDto.getIsActive());
        }
        if (candidateDto.getIsVerified() != null) {
            existingCandidate.setIsVerified(candidateDto.getIsVerified());
        }
        if (candidateDto.getVerificationStatus() != null) {
            existingCandidate.setVerificationStatus(candidateDto.getVerificationStatus());
        }
        if (candidateDto.getIsConsentProvided() != null) {
            existingCandidate.setIsConsentProvided(candidateDto.getIsConsentProvided());
        }
        
        Candidate updatedCandidate = candidateRepository.save(existingCandidate);
        return candidateMapper.toDto(updatedCandidate); // Using normal mapper
    }

    public void deleteCandidate(Long candidateId) {
        if (!candidateRepository.existsById(candidateId)) {
            throw new RuntimeException("Candidate not found with id: " + candidateId);
        }
        candidateRepository.deleteById(candidateId);
    }

    public void updateConsentStatus(Long candidateId, Boolean consentStatus) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + candidateId));
        
        candidate.setIsConsentProvided(consentStatus);
        candidateRepository.save(candidate);
    }

    public void updateVerificationStatus(Long candidateId, String verificationStatus) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + candidateId));
        
        candidate.setVerificationStatus(verificationStatus);
        candidate.setIsVerified("VERIFIED".equals(verificationStatus));
        candidateRepository.save(candidate);
    }

}
