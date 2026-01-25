package com.org.bgv.service;

import com.org.bgv.auth.dto.ResetPasswordRequest;
import com.org.bgv.auth.entity.PasswordResetToken;
import com.org.bgv.auth.service.ResetTokenService;
import com.org.bgv.common.CandidateDTO;
import com.org.bgv.common.ChangePasswordRequest;
import com.org.bgv.common.ColumnMetadata;
import com.org.bgv.common.FilterMetadata;
import com.org.bgv.common.FilterRequest;
import com.org.bgv.common.Option;
import com.org.bgv.common.PageRequestDto;
import com.org.bgv.common.PaginationMetadata;
import com.org.bgv.common.PaginationRequest;
import com.org.bgv.common.PaginationResponse;
import com.org.bgv.common.RoleConstants;
import com.org.bgv.common.SortField;
import com.org.bgv.common.SortingMetadata;
import com.org.bgv.common.SortingRequest;
import com.org.bgv.common.UserDto;
import com.org.bgv.common.UserSearchRequest;
import com.org.bgv.config.JwtUtil;
import com.org.bgv.controller.UserController;
import com.org.bgv.dto.UserDetailsDto;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.CompanyUser;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.Role;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserRole;
import com.org.bgv.entity.UserType;
import com.org.bgv.entity.Vendor;
import com.org.bgv.mapper.UserMapper;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.CompanyUserRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.RoleRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.UserRoleRepository;
import com.org.bgv.repository.VendorRepository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtUtil jwtUtil;
    private final ProfileService profileService;
    private final CompanyUserRepository companyUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final CandidateService candidateService;
    private final EmailService emailService;
    private final ProfileRepository profileRepository;
    private final VendorRepository vendorRepository;
    private final VerificationCaseService verificationCaseService;
    private final ResetTokenService resetTokenService;
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<UserDto> getAll() {
        try {
            return userRepository.findAll()
                    .stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch users: " + e.getMessage(), e);
        }
    }
    /*
    public PaginationResponse<UserDto> getAllUsers(PageRequestDto pageRequest) {
        Pageable pageable = createPageable(pageRequest);
        Page<User> userPage = userRepository.findAll(pageable);
        
        return buildCompletePaginationResponse(userPage, pageRequest);
    }
   */
    public PaginationResponse<UserDto> searchUsers(UserSearchRequest searchRequest) {
        
        Pageable pageable = createPageable(searchRequest.getPagination(), searchRequest.getSorting());
        
        // Build specification for filtering
        Specification<User> spec = buildSearchSpecification(searchRequest);
        
        Page<User> userPage = userRepository.findAll(spec, pageable);
        
        return buildCompletePaginationResponse(userPage, searchRequest);
    }
    
    private Pageable createPageable(PaginationRequest pagination, SortingRequest sorting) {
        if (sorting == null) {
            sorting = SortingRequest.builder()
                    .sortBy("userId")
                    .sortDirection("asc")
                    .build();
        }
        
        Sort sort = Sort.by(
            sorting.getSortDirection().equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC, 
            sorting.getSortBy()
        );
        return PageRequest.of(pagination.getPage(), pagination.getSize(), sort);
    }

    private Specification<User> buildSearchSpecification(UserSearchRequest searchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // For the modal (when we want to exclude users already in the company)
            if (searchRequest.getCompanyId() != null && searchRequest.getCompanyId() != 0 && searchRequest.isExcludeCompanyUsers()) {
                // Create a subquery to find users who are already in this company
                Subquery<Long> companyUsersSubquery = query.subquery(Long.class);
                Root<CompanyUser> companyUserRoot = companyUsersSubquery.from(CompanyUser.class);
                
                companyUsersSubquery.select(companyUserRoot.get("userId"))
                        .where(criteriaBuilder.equal(companyUserRoot.get("companyId"), searchRequest.getCompanyId()));
                
                // Exclude users who are already in the company
                predicates.add(criteriaBuilder.not(root.get("userId").in(companyUsersSubquery)));
            }
            // For the company users list (when we want to show only users in the company)
            else if (searchRequest.getCompanyId() != null && searchRequest.getCompanyId() != 0) {
                Join<User, CompanyUser> companyUserJoin = root.join("companyUsers", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(companyUserJoin.get("companyId"), searchRequest.getCompanyId()));
            }
            
            // Search term
            if (StringUtils.hasText(searchRequest.getSearch())) {
                String searchTerm = "%" + searchRequest.getSearch().toLowerCase() + "%";
                Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), searchTerm);
                Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), searchTerm);
                Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchTerm);
                Predicate phonePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), searchTerm);
                predicates.add(criteriaBuilder.or(firstNamePredicate, lastNamePredicate, emailPredicate, phonePredicate));
            }
            
            // Filters
            if (searchRequest.getFilters() != null) {
                for (FilterRequest filter : searchRequest.getFilters()) {
                    if (filter.getIsSelected() != null && filter.getIsSelected() && filter.getSelectedValue() != null) {
                        if (filter.getField().equals("isActive") || filter.getField().equals("isVerified")) {
                            // Handle boolean fields
                            Boolean value = Boolean.valueOf(filter.getSelectedValue().toString());
                            predicates.add(criteriaBuilder.equal(root.get(filter.getField()), value));
                        } else if (filter.getField().equals("userType")) {
                            // Handle enum fields
                            predicates.add(criteriaBuilder.equal(root.get(filter.getField()), filter.getSelectedValue()));
                        } else if (filter.getField().equals("createdDate")) {
                            // Handle date range - you might want to implement proper date range logic
                            predicates.add(criteriaBuilder.equal(root.get("createdAt"), filter.getSelectedValue()));
                        } else {
                            // Handle other string fields
                            predicates.add(criteriaBuilder.equal(root.get(filter.getField()), filter.getSelectedValue()));
                        }
                    }
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public UserDto getById(Long id) {
        try {
        	UserDto userDto = userRepository.findById(id)
                    .map(userMapper::toDto)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        	userDto.setRoles(getUserRoles(id));
        	
            return userDto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user: " + e.getMessage(), e);
        }
    }

    public UserDto create(UserDto userDto) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new RuntimeException("User with email " + userDto.getEmail() + " already exists");
            }

            User user = userMapper.toEntity(userDto);
            logger.info("in UserService:::::::::{}",user);
            User saved = userRepository.save(user);
            return userMapper.toDto(saved);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    public UserDto update(Long id, UserDetailsDto userDto) {
        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
            
            Profile profile = profileRepository.findByUserUserId(existingUser.getUserId());

            // Check if email is being changed and if it already exists for another user
            if (!existingUser.getEmail().equals(userDto.getEmail()) && 
                userRepository.existsByEmailAndUserIdNot(userDto.getEmail(), id)) {
                throw new RuntimeException("Email " + userDto.getEmail() + " already exists for another user");
            }

            // Update fields
            profile.setFirstName(userDto.getFirstName());
            profile.setLastName(userDto.getLastName());
            existingUser.setEmail(userDto.getEmail());
            
            // Update other fields as needed
            if (userDto.getPhoneNumber() != null) {
            	profile.setPhoneNumber(userDto.getPhoneNumber());
            }

            User updated = userRepository.save(existingUser);
            profileRepository.save(profile);
            return userMapper.toDto(updated);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    public void delete(Long id) {
        try {
            if (!userRepository.existsById(id)) {
                throw new RuntimeException("User not found with id: " + id);
            }
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    public UserDto getUserByEmail(String email) {
    	UserDto userDto=null;
    	try {
        	
    		userDto = userRepository.findByEmail(email)
                    .map(userMapper::toDto)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            userDto.setRoles(getUserRoles(userDto.getUserId()));
            
            return userDto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user by email: " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public void assignRoleToUser(Long userId, String roleName) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found with name: " + roleName));

            // Check if user already has this role
            boolean alreadyHasRole = user.getRoles().stream()
                    .anyMatch(userRole -> userRole.getRole().getName().equals(roleName));
            
            if (alreadyHasRole) {
                throw new RuntimeException("User already has role: " + roleName);
            }

            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();

            user.getRoles().add(userRole);
            userRoleRepository.save(userRole);
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign role to user: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void removeRoleFromUser(Long userId, String roleName) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            UserRole userRole = user.getRoles().stream()
                    .filter(ur -> ur.getRole().getName().equals(roleName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User does not have role: " + roleName));

            user.getRoles().remove(userRole);
            userRoleRepository.delete(userRole);
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove role from user: " + e.getMessage(), e);
        }
    }

    public List<String> getUserRoles(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
            return user.getRoles().stream()
                    .map(userRole -> userRole.getRole().getName())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user roles: " + e.getMessage(), e);
        }
    }

    public boolean userExists(Long id) {
        try {
            return userRepository.existsById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to check user existence: " + e.getMessage(), e);
        }
    }
    
    public UserDto getUserFromToken(String token) {
    	UserDto userDto = null;
try {
        if (jwtUtil.validateToken(token)) {
            String email = jwtUtil.getUsernameFromToken(token);
            userDto = getUserByEmail(email);

            // Try setting profileId, skip if not found
            try {
            	logger.info("userDto.getUserId()::::::::::::::::::::{}",userDto.getUserId());
                Long profileId = profileService.getProfileIdByUserId(userDto.getUserId());
                if (profileId != null) {
                    userDto.setProfileId(profileId);
                }
            } catch (Exception e) {
                System.out.println("⚠️ No profile found for userId: " + userDto.getUserId());
            }

            // Set company info if available
            List<CompanyUser> companyUsers = companyUserRepository.findByUserUserId(userDto.getUserId());
            if (companyUsers != null && !companyUsers.isEmpty()) {
                userDto.setCompanyId(companyUsers.get(0).getCompanyId());
            }
            if(userDto.getRoles().contains(RoleConstants.ROLE_CANDIDATE)) {
            	CandidateDTO candidate = candidateService.getCandidateByUserId(userDto.getUserId());
            	if(candidate!=null) {
            		userDto.setHasConsentProvided(candidate.getIsConsentProvided()==null?Boolean.FALSE:candidate.getIsConsentProvided());
            		userDto.setCandidateId(candidate.getCandidateId());
            		
            	}
            }else {
            	userDto.setHasConsentProvided(Boolean.TRUE);
            	
            }
            
            logger.info("############################################################################:::::::::::::{}",UserType.VENDOR.name());
            if(userDto.getUserType()!=null && userDto.getUserType().equalsIgnoreCase(UserType.VENDOR.name())) {
            	Vendor vendor = vendorRepository.findByUser_userId(userDto.getUserId());
            	logger.info("in user service:::::::::::{}",vendor);
            	userDto.setVendorId(vendor.getId());
            }
        }
			}catch (Exception e) {
				e.printStackTrace();
			}
        return userDto;
    }
    
    public void changePasswordByUser(Long userId, ChangePasswordRequest request) {
        logger.info("Changing password for user ID: {}", userId);

        // Validate request
        validateChangePasswordRequest(request);

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Check if new password is same as current password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("New password cannot be the same as current password");
        }

        // Update password
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedNewPassword);
        
        // Update timestamps if you have them
        user.setUpdatedAt(java.time.LocalDateTime.now());

        userRepository.save(user);
        
        logger.info("Password changed successfully for user: {}", user.getEmail());
    }
    
    
    public void changePasswordByAdmin(Long userId, ChangePasswordRequest request) {
        logger.info("Changing password for user ID: {}", userId);

        // Validate request
        validateChangePasswordRequest(request);

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
/*
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Check if new password is same as current password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("New password cannot be the same as current password");
        }
*/
        // Update password
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedNewPassword);
        
        // Update timestamps if you have them
        user.setUpdatedAt(java.time.LocalDateTime.now());

        userRepository.save(user);
        
        logger.info("Password changed successfully for user: {}", user.getEmail());
    }

    
    public void resetPassword(Long userId, ChangePasswordRequest request) {
    	logger.info("resetPassword:::::::::::::::::::::::::::::::");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        user.setPasswordResetrequired(Boolean.FALSE);

        userRepository.save(user);
        emailService.sendEmailResetPasswordSuccessfull(user);
        
        logger.info("Password reset successfully for user: {}", user.getEmail());
    }
    
    
    /**
     * Validate change password request
     */
    private void validateChangePasswordRequest(ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        if (request.getNewPassword().length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters long");
        }

        // Add more password strength validation if needed
        if (!isPasswordStrong(request.getNewPassword())) {
            throw new RuntimeException("Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character");
        }
    }
    
    public void resetPassword(String token, ResetPasswordRequest request) {

        validatePasswords(request);

        PasswordResetToken resetToken = resetTokenService.validateToken(token);

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + resetToken.getUserId()));

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        user.setPasswordResetrequired(Boolean.FALSE);

        userRepository.save(user);

        resetTokenService.markUsed(resetToken);
    }

    private void validatePasswords(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
    }
    
    

    /**
     * Password strength validation
     */
    private boolean isPasswordStrong(String password) {
        // At least one uppercase letter
        boolean hasUpper = !password.equals(password.toLowerCase());
        // At least one lowercase letter
        boolean hasLower = !password.equals(password.toUpperCase());
        // At least one digit
        boolean hasDigit = password.matches(".*\\d.*");
        // At least one special character
        boolean hasSpecial = !password.matches("[A-Za-z0-9]*");

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    @Transactional
    public void assignUsersToCompany(List<Long> userIds, Long companyId) {
    	 // Validate company exists
    	try {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));
        
        // Validate users exist
        List<User> users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new RuntimeException("One or more users not found");
        }
        
        // Assign users to company (avoid duplicates)
        LocalDateTime now = LocalDateTime.now();
        for (Long userId : userIds) {
            if (!companyUserRepository.existsByCompanyIdAndUserId(companyId, userId)) {
            	 User user = userRepository.findById(userId)
                         .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
              //  companyUserRepository.insertCompanyUser(companyId, userId, now);
            	CompanyUser companyUser = new CompanyUser();
            	companyUser.setCompany(company);
            	companyUser.setUser(user);
            	companyUserRepository.save(companyUser);
            }
        }
    }catch (Exception e) {
		e.printStackTrace();
	}
    }
    
    

    private Pageable createPageable(PageRequestDto pageRequest) {
        Sort sort = Sort.by(
            pageRequest.getSortDirection().equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC, 
            pageRequest.getSortBy()
        );
        return PageRequest.of(pageRequest.getPage(), pageRequest.getSize(), sort);
    }

    // Overloaded method for UserSearchRequest
    private PaginationResponse<UserDto> buildCompletePaginationResponse(Page<User> userPage, UserSearchRequest searchRequest) {
        List<UserDto> userDtos = userPage.getContent()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());

        // Build pagination metadata
        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
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
        List<ColumnMetadata> columns = null;
        
        if (searchRequest.getCompanyId() != null && searchRequest.getCompanyId() != 0 && searchRequest.isExcludeCompanyUsers()) {
        	columns = getColumnMetadataToAssignToCompany();
        }
        // For the company users list (when we want to show only users in the company)
        else if (searchRequest.getCompanyId() != null && searchRequest.getCompanyId() != 0) {
        	columns = getColumnMetadata();
        }else {
        	columns = getColumnMetadata();
        }

        return PaginationResponse.<UserDto>builder()
                .content(userDtos)
                .pagination(paginationMetadata)
                .sorting(sortingMetadata)
                .filters(filters)
                .columns(columns)
                .build();
    }

    // Original method for PageRequestDto
    /*
    private PaginationResponse<UserDto> buildCompletePaginationResponse(Page<User> userPage, PageRequestDto pageRequest) {
        List<UserDto> userDtos = userPage.getContent()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());

        // Build pagination metadata
        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .currentPage(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
                .allowedPageSizes(Arrays.asList(10, 25, 50, 100))
                .build();

        // Build sorting metadata
        SortingMetadata sortingMetadata = SortingMetadata.builder()
                .currentSort(SortField.builder()
                        .field(pageRequest.getSortBy())
                        .displayName(getDisplayNameForField(pageRequest.getSortBy()))
                        .direction(pageRequest.getSortDirection())
                        .build())
                .sortableFields(getSortableFields())
                .build();

        // Build filters metadata
        List<FilterMetadata> filters = getAvailableFilters();

        // Build columns metadata
        List<ColumnMetadata> columns = getColumnMetadata();
        
        zz

        return PaginationResponse.<UserDto>builder()
                .content(userDtos)
                .pagination(paginationMetadata)
                .sorting(sortingMetadata)
                .filters(filters)
                .columns(columns)
                .build();
    }
*/
    private List<SortField> getSortableFields() {
        return Arrays.asList(
            SortField.builder().field("userId").displayName("User ID").build(),
            SortField.builder().field("firstName").displayName("First Name").build(),
            SortField.builder().field("lastName").displayName("Last Name").build(),
            SortField.builder().field("email").displayName("Email").build(),
            SortField.builder().field("createdDate").displayName("Created Date").build(),
            SortField.builder().field("userType").displayName("User Type").build()
        );
    }

    // Overloaded method with selected filters
    private List<FilterMetadata> getAvailableFilters(UserSearchRequest searchRequest) {
        List<FilterMetadata> filters = new ArrayList<>();
        
        // User Type filter
        FilterMetadata userTypeFilter = FilterMetadata.builder()
                .field("userType")
                .displayName("User Type")
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

        return filters;
    }

    // Original method without selected filters
    private List<FilterMetadata> getAvailableFilters() {
        return getAvailableFilters(UserSearchRequest.builder().build());
    }

    private List<ColumnMetadata> getColumnMetadata() {
        return Arrays.asList(
            ColumnMetadata.builder().field("userId").displayName("User ID").visible(false).build(),
            ColumnMetadata.builder().field("name").displayName("Name").visible(true).build(),
            ColumnMetadata.builder().field("email").displayName("Email").visible(true).build(),
            ColumnMetadata.builder().field("userType").displayName("User Type").visible(true).build(),
            ColumnMetadata.builder().field("phoneNumber").displayName("Phone Number").visible(true).build(),
            ColumnMetadata.builder().field("isActive").displayName("Active").visible(true).build(),
            ColumnMetadata.builder().field("isVerified").displayName("Verified").visible(true).build(),
            ColumnMetadata.builder().field("status").displayName("Status").visible(true).build(),
            ColumnMetadata.builder().field("gender").displayName("Gender").visible(true).build(),
            ColumnMetadata.builder().field("dateOfBirth").displayName("Date of Birth").visible(false).build(),
            ColumnMetadata.builder().field("profilePictureUrl").displayName("Profile Picture").visible(false).build()
        );
    }
    private List<ColumnMetadata> getColumnMetadataToAssignToCompany() {
        return Arrays.asList(
           
            ColumnMetadata.builder().field("name").displayName("Name").visible(true).build(),
            ColumnMetadata.builder().field("email").displayName("Email").visible(true).build()
           
        );
    }

    private String getDisplayNameForField(String field) {
        switch (field) {
            case "userId": return "User ID";
            case "firstName": return "First Name";
            case "lastName": return "Last Name";
            case "email": return "Email";
            case "createdDate": return "Created Date";
            case "userType": return "User Type";
            default: return field;
        }
    }
}