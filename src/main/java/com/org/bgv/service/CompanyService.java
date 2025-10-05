package com.org.bgv.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.common.Status;
import com.org.bgv.company.dto.CompanyRegistrationRequestDTO;
import com.org.bgv.company.dto.CompanyRegistrationResponse;
import com.org.bgv.company.dto.EmployeeDTO;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.CompanyUser;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.CompanyUserRepository;
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
	   
	    
	    private final String FILE_UPLOAD_DIR = "uploads/profile-pictures/";

	    @Transactional
	    public CompanyRegistrationResponse registerCompany(CompanyRegistrationRequestDTO request) {
	        try {
	            log.info("Starting company registration for: {}", request.getCompanyName());
	            
	            // Validate passwords match
	            if (!request.getAdminPassword().equals(request.getAdminConfirmPassword())) {
	                throw new IllegalArgumentException("Passwords do not match");
	            }

	            // Check if company already exists
	            if (companyRepository.findByRegistrationNumber(request.getRegistrationNumber()).isPresent()) {
	                throw new IllegalArgumentException("Company with registration number " + request.getRegistrationNumber() + " already exists");
	            }

	            // Check if admin user already exists
	            if (userRepository.findByEmail(request.getAdminEmail()).isPresent()) {
	                throw new IllegalArgumentException("User with email " + request.getAdminEmail() + " already exists");
	            }
/*
	            // Handle file upload
	            String profilePicturePath = null;
	            if (request.getAdminProfilePicture() != null && !request.getAdminProfilePicture().isEmpty()) {
	                profilePicturePath = saveProfilePicture(request.getAdminProfilePicture());
	            }
*/
	            // Create and save Company
	            Company company = createCompanyFromRequest(request);
	            company = companyRepository.save(company);
	            log.info("Company created with ID: {}", company.getId());

	            // Create and save Admin User
	            User adminUser = createAdminUserFromRequest(request);
	            adminUser = userRepository.save(adminUser);
	            
	            Role companyAdmin = roleRepository.findByName(Status.ROLE_COMPANY_ADMIN)
	                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
	            
	            UserRole userRole = UserRole.builder()
	                    .user(adminUser)
	                    .role(companyAdmin)
	                    .build();
	            
	            userRoleRepository.save(userRole);
	            
	            
	            log.info("Admin user created with ID: {}", adminUser.getUserId());

	            // Create Company-User association
	            CompanyUser companyUser = new CompanyUser();
	            companyUser.setCompany(company);
	            companyUser.setUser(adminUser);
	            companyUserRepository.save(companyUser);
	            log.info("Company-User association created");

	            return CompanyRegistrationResponse.builder()
	                   // .success(true)
	                  //  .message("Company registered successfully")
	                    .companyId(company.getId())
	                    .adminUserId(adminUser.getUserId())
	                    .companyName(company.getCompanyName())
	                    .build();

	        } catch (Exception e) {
	            log.error("Error during company registration: {}", e.getMessage(), e);
	            throw new RuntimeException("Company registration failed: " + e.getMessage());
	        }
	    }

	    
	    /**
	     * Get all companies with pagination, sorting, and filtering
	     */
	    @Transactional(readOnly = true)
	    public Map<String, Object> getAllCompanies(Pageable pageable, String search, String industry, String status) {
	        log.info("Fetching companies with filters - search: {}, industry: {}, status: {}", 
	                search, industry, status);

	        // Create specification for filtering
	        Specification<Company> spec = buildSpecification(search, industry, status);

	        // Fetch paginated companies
	        Page<Company> companyPage = companyRepository.findAll(spec, pageable);

	        // Build response
	        Map<String, Object> response = new HashMap();
	        response.put("companies", companyPage.getContent());
	        response.put("currentPage", companyPage.getNumber());
	        response.put("totalItems", companyPage.getTotalElements());
	        response.put("totalPages", companyPage.getTotalPages());
	        response.put("hasNext", companyPage.hasNext());
	        response.put("hasPrevious", companyPage.hasPrevious());

	        log.info("Retrieved {} companies out of {} total", 
	                companyPage.getNumberOfElements(), companyPage.getTotalElements());

	        return response;
	    }
	    
	    
	    /**
	     * Build dynamic query specification based on filters
	     */
	    private Specification<Company> buildSpecification(String search, String industry, String status) {
	        return (root, query, criteriaBuilder) -> {
	            List<Predicate> predicates = new ArrayList<>();

	            // Search filter (company name, registration number, contact email, contact person)
	            if (search != null && !search.trim().isEmpty()) {
	                String searchPattern = "%" + search.toLowerCase() + "%";
	                Predicate searchPredicate = criteriaBuilder.or(
	                    criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")), searchPattern),
	                    criteriaBuilder.like(criteriaBuilder.lower(root.get("registrationNumber")), searchPattern),
	                    criteriaBuilder.like(criteriaBuilder.lower(root.get("contactEmail")), searchPattern),
	                    criteriaBuilder.like(criteriaBuilder.lower(root.get("contactPersonName")), searchPattern)
	                );
	                predicates.add(searchPredicate);
	            }

	            // Industry filter
	            if (industry != null && !industry.trim().isEmpty()) {
	                predicates.add(criteriaBuilder.equal(root.get("industry"), industry));
	            }

	            // Status filter
	            if (status != null && !status.trim().isEmpty()) {
	                predicates.add(criteriaBuilder.equal(root.get("status"), status.toUpperCase()));
	            }

	            // Only return non-deleted companies (if you have soft delete)
	            // predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

	            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	        };
	    }

	    /**
	     * Get company by ID
	     */
	    @Transactional(readOnly = true)
	    public Company getCompanyById(Long id) {
	        log.info("Fetching company by ID: {}", id);
	        
	        Optional<Company> company = companyRepository.findById(id);
	        
	        if (company.isEmpty()) {
	            throw new RuntimeException("Company not found with ID: " + id);
	        }
	        
	        return company.get();
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
	    
	    public Boolean addEmployee(Long companyId, EmployeeDTO employeeDTO, String status) {

	        // Create new User entity from DTO
	        User user = User.builder()
	                .firstName(employeeDTO.getFirstName())
	                .lastName(employeeDTO.getLastName())
	                .phoneNumber(employeeDTO.getMobileNo())
	                .email(employeeDTO.getEmail())
	                .gender(employeeDTO.getGender())
	               // .password(UUID.randomUUID().toString())
	                .password(passwordEncoder.encode("123456"))
	                .userType(Status.USER_TYPE_COMPANY)
	                .status(status)
	                .build();

	        // Save user first (if User is a new entity)
	        userRepository.save(user);

	        // Find Role
	        Role companyRole = roleRepository.findByName(Status.ROLE_COMPANY_HR_MANAGER)
	                .orElseThrow(() -> new RuntimeException("ROLE_COMPANY_HR_MANAGER not found"));

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

	        return true;
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

	    private User createAdminUserFromRequest(CompanyRegistrationRequestDTO request) {
	        return User.builder()
	                .firstName(request.getAdminFirstName())
	                .lastName(request.getAdminLastName())
	                .email(request.getAdminEmail())
	                .phoneNumber(request.getAdminPhone())
	                .password(passwordEncoder.encode(request.getAdminPassword()))
	                .userType(Status.USER_TYPE_COMPANY)
	                .isActive(true)
	                .isVerified(false) // Email verification can be done later
	               // .profilePictureUrl(profilePicturePath)
	                .createdAt(LocalDateTime.now())
	                .updatedAt(LocalDateTime.now())
	                .build();
	    }
/*
	    private String saveProfilePicture(MultipartFile file) {
	        try {
	            // Create upload directory if it doesn't exist
	            Path uploadPath = Paths.get(FILE_UPLOAD_DIR);
	            if (!Files.exists(uploadPath)) {
	                Files.createDirectories(uploadPath);
	            }

	            // Generate unique filename
	            String originalFileName = file.getOriginalFilename();
	            String fileExtension = originalFileName != null ? 
	                originalFileName.substring(originalFileName.lastIndexOf(".")) : ".jpg";
	            String fileName = UUID.randomUUID().toString() + fileExtension;

	            // Save file
	            Path filePath = uploadPath.resolve(fileName);
	            Files.copy(file.getInputStream(), filePath);

	            return FILE_UPLOAD_DIR + fileName;

	        } catch (IOException e) {
	            log.error("Failed to save profile picture: {}", e.getMessage());
	            throw new RuntimeException("Failed to save profile picture");
	        }
	    }
*/
	    public boolean isRegistrationNumberExists(String registrationNumber) {
	        return companyRepository.findByRegistrationNumber(registrationNumber).isPresent();
	    }

	    public boolean isAdminEmailExists(String email) {
	        return userRepository.findByEmail(email).isPresent();
	    }
}
