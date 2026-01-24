package com.org.bgv.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.org.bgv.common.ColumnMetadata;
import com.org.bgv.common.CommonUtils;
import com.org.bgv.common.CompanySearchRequest;
import com.org.bgv.common.FilterMetadata;
import com.org.bgv.common.FilterRequest;
import com.org.bgv.common.Option;
import com.org.bgv.common.PaginationMetadata;
import com.org.bgv.common.PaginationRequest;
import com.org.bgv.common.PaginationResponse;
import com.org.bgv.common.SortField;
import com.org.bgv.common.SortingMetadata;
import com.org.bgv.common.SortingRequest;
import com.org.bgv.common.Status;
import com.org.bgv.common.UserDto;
import com.org.bgv.common.UserSearchRequest;
import com.org.bgv.company.dto.CompanyDetailsDTO;
import com.org.bgv.company.dto.CompanyRegistrationRequestDTO;
import com.org.bgv.company.dto.CompanyRegistrationResponse;
import com.org.bgv.company.dto.EmployeeDTO;
import com.org.bgv.company.dto.EmployerDTO;
import com.org.bgv.company.dto.PersonDTO;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.CompanyUser;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.CompanyUserRepository;
import com.org.bgv.repository.EmailTemplateRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.RoleRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.UserRoleRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.Builder;
import lombok.RequiredArgsConstructor;


@Builder
@RequiredArgsConstructor
@Service
public class CompanyService {
	
	private static final Logger log = LoggerFactory.getLogger(CompanyService.class);
	
	    private final CompanyRepository companyRepository;
	    private final UserRepository userRepository;
	    private final CompanyUserRepository companyUserRepository;
	    private final PasswordEncoder passwordEncoder;
	    private final RoleRepository roleRepository;
	    private final UserRoleRepository userRoleRepository;
	    private final ProfileService profileService;
	    private final ProfileRepository profileRepository;
	    private final EmailTemplateRepository emailTemplateRepository;
	    private final EmailService emailService;
	   
	    
	  //  private final String FILE_UPLOAD_DIR = "uploads/profile-pictures/";

	    @Transactional
	    public CompanyRegistrationResponse registerCompany(CompanyRegistrationRequestDTO request) {
	        try {
	            log.info("Starting company registration for: {}", request.getCompanyName());

	            // Check if company already exists
	            if (companyRepository.findByRegistrationNumber(request.getRegistrationNumber()).isPresent()) {
	                throw new IllegalArgumentException("Company with registration number " + request.getRegistrationNumber() + " already exists");
	            }

	            // Create and save Company
	            Company company = createCompanyFromRequest(request);
	            company = companyRepository.save(company);
	            log.info("Company created with ID: {}", company.getId());
	            return CompanyRegistrationResponse.builder()
	                   // .success(true)
	                  //  .message("Company registered successfully")
	                    .companyId(company.getId())
	                   // .adminUserId(adminUser.getUserId())
	                    .companyName(company.getCompanyName())
	                    .build();

	        } catch (Exception e) {
	            log.error("Error during company registration: {}", e.getMessage(), e);
	            throw new RuntimeException("Company registration failed: " + e.getMessage());
	        }
	    }
	    @Transactional
	    public CompanyRegistrationResponse updateCompany(CompanyRegistrationRequestDTO request) {
	        try {
	            log.info("Starting company updating for: {}", request);

	            Company existingCompany = companyRepository.findById(request.getId())
	                    .orElseThrow(() -> new IllegalArgumentException("Company with ID " + request.getId() + " not found"));
	         
	           

	            // Create and save Company
	            existingCompany = updateCompanyFromRequest(existingCompany, request);
	            existingCompany = companyRepository.save(existingCompany);
	            log.info("Company created with ID: {}", existingCompany.getId());
	            return CompanyRegistrationResponse.builder()
	                   // .success(true)
	                  //  .message("Company registered successfully")
	                    .companyId(existingCompany.getId())
	                   // .adminUserId(adminUser.getUserId())
	                    .companyName(existingCompany.getCompanyName())
	                    .build();

	        } catch (Exception e) {
	            log.error("Error during company registration: {}", e.getMessage(), e);
	            throw new RuntimeException("Company registration failed: " + e.getMessage());
	        }
	    }
	    
	   

	    /**
	     * Get company by ID
	     */
	    @Transactional(readOnly = true)
	    public CompanyDetailsDTO getCompanyById(Long id) {
	        log.info("Fetching company by ID: {}", id);
	        
	        Optional<Company> company = companyRepository.findById(id);
	        
	        if (company.isEmpty()) {
	            throw new RuntimeException("Company not found with ID: " + id);
	        }
	        return convertToCompanyDetailsDTO(company.get());
	       
	    }

	    /**
	     * Get companies count for dashboard
	     */
	    @Transactional(readOnly = true)
	    public Map<String, Long> getCompaniesCount() {
	        log.info("Fetching companies count for dashboard");
	        
	        Map<String, Long> counts = new HashMap<>();
	        
	        // Total companies
	        long totalCompanies = companyRepository.count();
	        counts.put("total", totalCompanies);
	        
	        // Count by status
	        long activeCompanies = companyRepository.countByStatus("active");
	        long inactiveCompanies = companyRepository.countByStatus("inactive");
	        long pendingCompanies = companyRepository.countByStatus("pending");
	        
	        counts.put("active", activeCompanies);
	        counts.put("inactive", inactiveCompanies);
	        counts.put("pending", pendingCompanies);
	        
	        log.info("Companies count - Total: {}, Active: {}, Inactive: {}, Pending: {}", 
	                totalCompanies, activeCompanies, inactiveCompanies, pendingCompanies);
	        
	        return counts;
	    }

	    /**
	     * Update company status
	     */
	    @Transactional
	    public Company updateCompanyStatus(Long id, String status) {
	        log.info("Updating company status - ID: {}, new status: {}", id, status);
	        
	        Optional<Company> companyOpt = companyRepository.findById(id);
	        
	        if (companyOpt.isEmpty()) {
	            throw new RuntimeException("Company not found with ID: " + id);
	        }
	        
	        Company company = companyOpt.get();
	        company.setStatus(status);
	        
	        Company updatedCompany = companyRepository.save(company);
	        log.info("Company status updated successfully - ID: {}, new status: {}", id, status);
	        
	        return updatedCompany;
	    }
	    
	    public Boolean addEmployee(Long companyId, EmployeeDTO employeeDTO,String type) {
	    	log.info("addEmployee::::::::::::::::::::::STARTED");
	    	   	    		    	
	    	String tempPassword = CommonUtils.generateTempPassword();
	    	User user = User.builder()
	              //  .firstName(employeeDTO.getFirstName())
	              //  .lastName(employeeDTO.getLastName())
	             //   .phoneNumber(employeeDTO.getMobileNo())
	                .email(employeeDTO.getEmail())
	             //   .gender(employeeDTO.getGender())
	               // .password(UUID.randomUUID().toString())
	                .password(passwordEncoder.encode(tempPassword))
	                .userType(type)
	                .passwordResetrequired(Boolean.TRUE)
	                .status(employeeDTO.getStatus())
	                .build();

	        // Save user first (if User is a new entity)
	        userRepository.save(user);
	        // Find Company
	        Company company = companyRepository.findById(companyId)
	                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

	        // Create CompanyUser mapping
	        CompanyUser companyUser = new CompanyUser();
	        companyUser.setCompany(company);
	        companyUser.setUser(user);

	        companyUserRepository.save(companyUser);

	        Profile profile =  Profile.builder()
            //  .profileId(dto.getBasicDetails().getProfileId())
              .firstName(employeeDTO.getFirstName())
              .lastName(employeeDTO.getLastName())
              .emailAddress(employeeDTO.getEmail())
              .phoneNumber(employeeDTO.getMobileNo())
             // .dateOfBirth(employeeDTO.getDateOfBirth())
              .gender(employeeDTO.getGender())
             // .userId(dto.getUser_id())
              .user(user)
              .status(employeeDTO.getStatus())
              .build();
			
	        profileRepository.save(profile);
	        
	        emailService.sendEmailToEmployeeRegistrationSuccess(user,tempPassword);

	        return true;
	    }


	    public Boolean addCandidate(Long companyId, PersonDTO employeeDTO) {
	    	log.info("addCandidate::::::::::::::::::::::STARTED");
	    	
	    	String roleName=Status.ROLE_CANDIDATE;
	    		    	
	    	if(employeeDTO.getStatus().equalsIgnoreCase("ACTIVE")) {
	    		log.info("addPerson::::::::::::employeeDTO.getStatus()::::::::::{}",employeeDTO.getStatus());
	    		if(employeeDTO.getId()==null) {
	    			log.info("addPerson::::::::::::employeeDTO.getId()::::::::::{}",employeeDTO.getId());
	    			 // Create new User entity from DTO
	    	        User user = User.builder()
	    	              //  .firstName(employeeDTO.getFirstName())
	    	              //  .lastName(employeeDTO.getLastName())
	    	              //  .phoneNumber(employeeDTO.getMobileNo())
	    	                .email(employeeDTO.getEmail())
	    	             //   .gender(employeeDTO.getGender())
	    	               // .password(UUID.randomUUID().toString())
	    	                .password(passwordEncoder.encode("123456"))
	    	                .userType(Status.USER_TYPE_CANDIDATE)
	    	                .status(employeeDTO.getStatus())
	    	                .build();

	    	        // Save user first (if User is a new entity)
	    	        userRepository.save(user);

	    	        // Find Role
	    	        Role companyRole = roleRepository.findByName(roleName)
	    	                .orElseThrow(() -> new RuntimeException(employeeDTO.getRole() + " not found"));

	    	        // Create UserRole mapping
	    	        UserRole userRole = UserRole.builder()
	    	                .user(user)
	    	                .role(companyRole)
	    	                .build();

	    	        userRoleRepository.save(userRole);

	    	        // Find Company
	    	        Company company = companyRepository.findById(companyId)
	    	                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

	    	        // Create CompanyUser mapping
	    	        CompanyUser companyUser = new CompanyUser();
	    	        companyUser.setCompany(company);
	    	        companyUser.setUser(user);

	    	        companyUserRepository.save(companyUser);
	    	        
	    	        Profile profile =  Profile.builder()
	                //  .profileId(dto.getBasicDetails().getProfileId())
	                  .firstName(employeeDTO.getFirstName())
	                  .lastName(employeeDTO.getLastName())
	                  .emailAddress(employeeDTO.getEmail())
	                  .phoneNumber(employeeDTO.getMobileNo())
	                 // .dateOfBirth(employeeDTO.getDateOfBirth())
	                  .gender(employeeDTO.getGender())
	                 // .userId(dto.getUser_id())
	                  .user(user)
	                  .status(employeeDTO.getStatus())
	                  .build();
	    			
	    	        profileRepository.save(profile);
	    	        
	    	        // save candidate info
	    		}else {
	    			
	    			 User user = userRepository.findById(employeeDTO.getId())
	    	                 .orElseThrow(() -> new RuntimeException("User not found: " + employeeDTO.getId()));
	    			
	    			 Profile profile=Profile.builder()
		                //  .profileId(dto.getBasicDetails().getProfileId())
		                  .firstName(employeeDTO.getFirstName())
		                  .lastName(employeeDTO.getLastName())
		                  .emailAddress(employeeDTO.getEmail())
		                  .phoneNumber(employeeDTO.getMobileNo())
		                 // .dateOfBirth(employeeDTO.getDateOfBirth())
		                  .gender(employeeDTO.getGender())
		                 // .userId(dto.getUser_id())
		                  .user(user)
		                  .status(employeeDTO.getStatus())
		                  .build();
	    			 profileRepository.save(profile);
	    		}
	    		
	    	}else {
	    		 // Create new User entity from DTO
    	        User user = User.builder()
    	              //  .firstName(employeeDTO.getFirstName())
    	             //   .lastName(employeeDTO.getLastName())
    	             //   .phoneNumber(employeeDTO.getMobileNo())
    	                .email(employeeDTO.getEmail())
    	             //   .gender(employeeDTO.getGender())
    	               // .password(UUID.randomUUID().toString())
    	                .password(passwordEncoder.encode("123456"))
    	                .userType(Status.USER_TYPE_CANDIDATE)
    	                .status(employeeDTO.getStatus())
    	                .build();

    	        // Save user first (if User is a new entity)
    	        userRepository.save(user);

    	        // Find Role
    	        Role companyRole = roleRepository.findByName(roleName)
    	                .orElseThrow(() -> new RuntimeException(employeeDTO.getRole() + " not found"));

    	        // Create UserRole mapping
    	        UserRole userRole = UserRole.builder()
    	                .user(user)
    	                .role(companyRole)
    	                .build();

    	        userRoleRepository.save(userRole);

    	        // Find Company
    	        Company company = companyRepository.findById(companyId)
    	                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

    	        // Create CompanyUser mapping
    	        CompanyUser companyUser = new CompanyUser();
    	        companyUser.setCompany(company);
    	        companyUser.setUser(user);

    	        companyUserRepository.save(companyUser);
	    		
	    	}

	        return true;
	    }
	    
	    @Transactional
	    public void removeUserFromCompany(List<Long> userIds, Long companyId) {
	        if (userIds == null || userIds.isEmpty()) {
	            return; // Or throw exception: throw new IllegalArgumentException("User IDs list cannot be null or empty");
	        }
	        
	        for (Long userId : userIds) {
	            CompanyUser companyUser = companyUserRepository.findByCompanyIdAndUserId(companyId, userId)
	                .orElseThrow(() -> new RuntimeException(
	                    String.format("User with ID %d not found in company with ID %d", userId, companyId)
	                ));
	            
	            companyUserRepository.delete(companyUser);
	        }
	        
	        // Optional: Log the operation
	        log.info("Removed {} users from company {}", userIds.size(), companyId);
	    }
	    
	    
	    
	    private Company createCompanyFromRequest(CompanyRegistrationRequestDTO request) {
	        Company company = new Company();
	        
	        // Company Information
	        company.setCompanyName(request.getCompanyName());
	        company.setCompanyType(request.getCompanyType());
	        company.setRegistrationNumber(request.getRegistrationNumber());
	        company.setTaxId(request.getTaxId());
	        company.setIncorporationDate(request.getIncorporationDate());
	        company.setIndustry(request.getIndustry());
	        company.setCompanySize(request.getCompanySize());
	        company.setWebsite(request.getWebsite());
	        company.setDescription(request.getDescription());
	        
	        // Contact Information
	        company.setContactPersonName(request.getContactPersonName());
	        company.setContactPersonTitle(request.getContactPersonTitle());
	        company.setContactEmail(request.getContactEmail());
	        company.setContactPhone(request.getContactPhone());
	        
	        // Address Information
	        company.setAddressLine1(request.getAddressLine1());
	        company.setAddressLine2(request.getAddressLine2());
	        company.setCity(request.getCity());
	        company.setState(request.getState());
	        company.setCountry(request.getCountry());
	        company.setZipCode(request.getZipCode());
	        
	        // Additional Information
	        company.setLinkedinProfile(request.getLinkedinProfile());
	     //   company.setAdminProfilePicturePath(profilePicturePath);
	        company.setStatus(Status.PENDING);
	        
	        return company;
	    }
	    private Company updateCompanyFromRequest(Company company, CompanyRegistrationRequestDTO request) {
	        // Update company fields
	        company.setCompanyName(request.getCompanyName());
	        company.setCompanyType(request.getCompanyType());
	        company.setRegistrationNumber(request.getRegistrationNumber());
	        company.setTaxId(request.getTaxId());
	        company.setIncorporationDate(request.getIncorporationDate());
	        company.setIndustry(request.getIndustry());
	        company.setCompanySize(request.getCompanySize());
	        company.setWebsite(request.getWebsite());
	        company.setDescription(request.getDescription());
	        
	        // Update contact information
	        company.setContactPersonName(request.getContactPersonName());
	        company.setContactPersonTitle(request.getContactPersonTitle());
	        company.setContactEmail(request.getContactEmail());
	        company.setContactPhone(request.getContactPhone());
	        
	        // Update address information
	        company.setAddressLine1(request.getAddressLine1());
	        company.setAddressLine2(request.getAddressLine2());
	        company.setCity(request.getCity());
	        company.setState(request.getState());
	        company.setCountry(request.getCountry());
	        company.setZipCode(request.getZipCode());
	        
	        // Update additional information
	        company.setLinkedinProfile(request.getLinkedinProfile());
	        company.setStatus(request.getStatus());
	        
	        // Set updated timestamp
	        company.setUpdatedAt(LocalDateTime.now());
			return company;
	    }
	    

	    public boolean isRegistrationNumberExists(String registrationNumber) {
	        return companyRepository.findByRegistrationNumber(registrationNumber).isPresent();
	    }

	    public boolean isAdminEmailExists(String email) {
	        return userRepository.findByEmail(email).isPresent();
	    }
	    
	    public PaginationResponse<EmployerDTO> searchUsers(CompanySearchRequest searchRequest) {
	        // Build pageable
	    	 log.info("Company service:::::::before ::createPageable:::");
	        Pageable pageable = createPageable(searchRequest.getPagination(), searchRequest.getSorting());
	        log.info("Company service:::::::after ::createPageable:::pageable{}",pageable);
	        // Build specification for filtering
	        Specification<Company> spec = buildSearchSpecification(searchRequest);
	        
	        Page<Company> companyPage = companyRepository.findAll(spec, pageable);
	        
	        return buildCompletePaginationResponse(companyPage, searchRequest);
	    }
	    
	    private PaginationResponse<EmployerDTO> buildCompletePaginationResponse(Page<Company> companyPage, CompanySearchRequest searchRequest) {
	        List<EmployerDTO> employerDtos = companyPage.getContent()
	                .stream()
	                .map(m -> mapToEmployerDtoFromEntity(m))
	                .collect(Collectors.toList());

	        // Build pagination metadata
	        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
	                .currentPage(companyPage.getNumber())
	                .pageSize(companyPage.getSize())
	                .totalElements(companyPage.getTotalElements())
	                .totalPages(companyPage.getTotalPages())
	                .hasNext(companyPage.hasNext())
	                .hasPrevious(companyPage.hasPrevious())
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
	        List<ColumnMetadata> columns = getColumnMetadata();

	        return PaginationResponse.<EmployerDTO>builder()
	                .content(employerDtos)
	                .pagination(paginationMetadata)
	                .sorting(sortingMetadata)
	                .filters(filters)
	                .columns(columns)
	                .build();
	    }
	    private String getDisplayNameForField(String field) {
	        switch (field) {
	            case "companyName": return "Company Name";
	            default: return field;
	        }
	    }
	    private Specification<Company> buildSearchSpecification(CompanySearchRequest searchRequest) {
	        return (root, query, criteriaBuilder) -> {
	            List<Predicate> predicates = new ArrayList<>();
	            
	            // Exclude default company
	            Predicate notDefaultCompany = criteriaBuilder.notEqual(root.get("companyName"), "default");
	            predicates.add(notDefaultCompany);
	            
	            // Search term
	            if (StringUtils.hasText(searchRequest.getSearch())) {
	                log.info("StringUtils.hasText(searchRequest.getSearch())::::::{}", StringUtils.hasText(searchRequest.getSearch()));
	                String searchTerm = "%" + searchRequest.getSearch().toLowerCase() + "%";
	                Predicate searchPredicate = criteriaBuilder.or(
	                    criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")), searchTerm),
	                    criteriaBuilder.like(criteriaBuilder.lower(root.get("registrationNumber")), searchTerm),
	                    criteriaBuilder.like(criteriaBuilder.lower(root.get("contactEmail")), searchTerm),
	                    criteriaBuilder.like(criteriaBuilder.lower(root.get("contactPersonName")), searchTerm)
	                );
	                predicates.add(searchPredicate);
	            }
	            
	            // Filters
	            // Add your other filters here if needed
	            
	            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	        };
	    }
	    
	    private List<ColumnMetadata> getColumnMetadata() {
	        return Arrays.asList(
	            ColumnMetadata.builder().field("Company").displayName("Company").visible(false).build(),
	            ColumnMetadata.builder().field("Contact").displayName("Contact").visible(true).build(),
	            ColumnMetadata.builder().field("Industry").displayName("Industry").visible(true).build(),
	            ColumnMetadata.builder().field("Size").displayName("Size").visible(true).build(),
	            ColumnMetadata.builder().field("Status").displayName("Status").visible(true).build()
	           
	        );
	    }
	    
	    private Pageable createPageable(PaginationRequest pagination, SortingRequest sorting) {
	    	
	    	if (sorting == null) {
	            sorting = SortingRequest.builder()
	                    .sortBy("companyName")
	                    .sortDirection("asc")
	                    .build();
	        }
	        log.info("Company sevice:::::createPageable:::::{}",sorting);
	        Sort sort = Sort.by(
	            sorting.getSortDirection().equalsIgnoreCase("desc") ? 
	            Sort.Direction.DESC : Sort.Direction.ASC, 
	            sorting.getSortBy()
	        );
	        
	        log.info("Company sevice:::::createPageable::sort::pagination:{}",pagination);
	        return PageRequest.of(pagination.getPage(), pagination.getSize(), sort);
	    }
	    
	    private List<FilterMetadata> getAvailableFilters(CompanySearchRequest searchRequest) {
	        List<FilterMetadata> filters = new ArrayList<>();
	        
	        /*
	        // User Type filter
	        FilterMetadata userTypeFilter = FilterMetadata.builder()
	                .field("companyName")
	                .displayName("Company Name")
	                .type("dropdown")
	                .options(Arrays.asList(
	                    Option.builder().label("Admin").value("ADMIN").build(),
	                    Option.builder().label("Vendor").value("VENDOR").build(),
	                    Option.builder().label("Company").value("COMPANY").build(),
	                    Option.builder().label("Employee").value("EMPLOYEE").build()
	                ))
	                .build();

	        // Status filter
	        FilterMetadata statusFilter = FilterMetadata.builder()
	                .field("isActive")
	                .displayName("Status")
	                .type("dropdown")
	                .options(Arrays.asList(
	                    Option.builder().label("Active").value("true").build(),
	                    Option.builder().label("Inactive").value("false").build()
	                ))
	                .build();

	        // Verification status filter
	        FilterMetadata verificationFilter = FilterMetadata.builder()
	                .field("isVerified")
	                .displayName("Verification Status")
	                .type("dropdown")
	                .options(Arrays.asList(
	                    Option.builder().label("Verified").value("true").build(),
	                    Option.builder().label("Not Verified").value("false").build()
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
	                        case "userType":
	                            userTypeFilter.setSelectedValue(filter.getSelectedValue());
	                            break;
	                        case "isActive":
	                            statusFilter.setSelectedValue(filter.getSelectedValue());
	                            break;
	                        case "isVerified":
	                            verificationFilter.setSelectedValue(filter.getSelectedValue());
	                            break;
	                        case "createdDate":
	                            dateFilter.setSelectedValue(filter.getSelectedValue());
	                            break;
	                    }
	                }
	            }
	        }

	        filters.add(userTypeFilter);
	        filters.add(statusFilter);
	        filters.add(verificationFilter);
	        filters.add(dateFilter);
*/
	        return filters;
	    }
	    private List<SortField> getSortableFields() {
	        return Arrays.asList(
	            SortField.builder().field("companyName").displayName("Company Name").build()
	           
	        );
	    }
	    
	    private EmployerDTO mapToEmployerDtoFromEntity(Company company) {
	    	log.info("mapToEmployerDtoFromEntity::::::::::::::");
	    	return EmployerDTO.builder()
	    			.id(company.getId())
	    	.companyname(company.getCompanyName())
	    	.companySize(company.getCompanySize())
	    	.status(company.getStatus())
	    	.industry(company.getIndustry())
	    	.companyType(company.getCompanyType())
	    	.build();
	    	
	    }
	    
	    private CompanyDetailsDTO convertToCompanyDetailsDTO(Company company) {
	        return CompanyDetailsDTO.builder()
	                // Company Information
	                .companyName(company.getCompanyName())
	                .companyType(company.getCompanyType())
	                .registrationNumber(company.getRegistrationNumber())
	                .taxId(company.getTaxId())
	                .incorporationDate(company.getIncorporationDate())
	                .industry(company.getIndustry())
	                .companySize(company.getCompanySize())
	                .website(company.getWebsite())
	                .description(company.getDescription())
	                
	                // Contact Information
	                .contactPersonName(company.getContactPersonName())
	                .contactPersonTitle(company.getContactPersonTitle())
	                .contactEmail(company.getContactEmail())
	                .contactPhone(company.getContactPhone())
	                
	                // Address Information
	                .addressLine1(company.getAddressLine1())
	                .addressLine2(company.getAddressLine2())
	                .city(company.getCity())
	                .state(company.getState())
	                .country(company.getCountry())
	                .zipCode(company.getZipCode())
	                
	                // Additional Information
	                .linkedinProfile(company.getLinkedinProfile())
	                .status(company.getStatus())
	                .build();
	    }
}
