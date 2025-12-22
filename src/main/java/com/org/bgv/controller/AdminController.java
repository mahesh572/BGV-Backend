package com.org.bgv.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.AssignUsersRequest;
import com.org.bgv.common.ChangePasswordRequest;
import com.org.bgv.common.CompanySearchRequest;
import com.org.bgv.common.PaginationResponse;
import com.org.bgv.common.RoleConstants;
import com.org.bgv.common.UserDto;
import com.org.bgv.common.UserSearchRequest;
import com.org.bgv.company.dto.CompanyDetailsDTO;
import com.org.bgv.company.dto.CompanyRegistrationRequestDTO;
import com.org.bgv.company.dto.CompanyRegistrationResponse;
import com.org.bgv.company.dto.EmployerDTO;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.dto.BasicdetailsDTO;
import com.org.bgv.dto.UserDetailsDto;
import com.org.bgv.entity.Company;
import com.org.bgv.mapper.UserMapper;
import com.org.bgv.service.CompanyService;
import com.org.bgv.service.ProfileService;
import com.org.bgv.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
	
	  private final UserService userService;
	  private final ProfileService profileService;
	  private final UserMapper userMapper;
	  private final CompanyService companyService;
	
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	 	@PostMapping
	    public ResponseEntity<CustomApiResponse<UserDto>> create(@RequestBody UserDto userDto) {
	        try {
	        	logger.info("users/create::::::{}",userDto);
	        //	userDto.setUserType(Constants.USER_TYPE_CANDIDATE);
	        	UserDto createdUser = userService.create(userDto);
	        	logger.info("AdminController:::::createdUser::{}",createdUser);
	        	userDto.setUserId(createdUser.getUserId());
	        	// Profile creation
	        	BasicdetailsDTO basicDetailsDTO = userMapper.mapUserDTOToBasicdetails(userDto);
	        	logger.info("AdminController::::::profileservice:::::START:::::{}",basicDetailsDTO);
	        	basicDetailsDTO = profileService.createProfile(basicDetailsDTO);
	        	
	            return ResponseEntity.status(HttpStatus.CREATED)
	                    .body(CustomApiResponse.success("User created successfully", createdUser, HttpStatus.CREATED));
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(CustomApiResponse.failure("Failed to create user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
	        }
	    }

	 	
	 	
	 	@PostMapping("/users/search")
	    public ResponseEntity<CustomApiResponse<PaginationResponse<UserDto>>> searchUsers(
	            @RequestBody UserSearchRequest searchRequest) {
	    	logger.info("searchUsers::::::::::::::::::::::{}"+searchRequest);
	        try {
	        	
	        	if(SecurityUtils.hasRole(RoleConstants.ROLE_COMAPNY_ADMINISTRATOR)) {
	        		logger.info("RoleConstants.ROLE_COMAPNY_ADMINISTRATOR::::::::::::::");
	        		Long companyId = SecurityUtils.getCurrentUserCompanyId();
	        		logger.info("RoleConstants.ROLE_COMAPNY_ADMINISTRATOR::::::::::companyId::::{}",companyId);
	        		searchRequest.setCompanyId(companyId);
	        	}
	        	
	        	
	            PaginationResponse<UserDto> response = userService.searchUsers(searchRequest);
	            return ResponseEntity.ok(CustomApiResponse.success("Users retrieved successfully", response, HttpStatus.OK));
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(CustomApiResponse.failure("Failed to search users: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
	        }
	    }
	 	
	 	@PostMapping("/company/{companyId}/users/search")
	    public ResponseEntity<CustomApiResponse<PaginationResponse<UserDto>>> searchUsersByCompany(
	            @RequestBody UserSearchRequest searchRequest,
	            @PathVariable Long companyId) {
	    	logger.info("searchUsers::::::::::::::::::::::{}"+searchRequest);
	        try {
	        	if(companyId!=null && companyId!=0) {
	        		searchRequest.setCompanyId(companyId);
	        	}
	            PaginationResponse<UserDto> response = userService.searchUsers(searchRequest);
	            return ResponseEntity.ok(CustomApiResponse.success("Users retrieved successfully", response, HttpStatus.OK));
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(CustomApiResponse.failure("Failed to search users: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
	        }
	    }
	 	@PostMapping("/company/{companyId}/users/search/assign")
	    public ResponseEntity<CustomApiResponse<PaginationResponse<UserDto>>> searchUsersByCompanyToAssign(
	            @RequestBody UserSearchRequest searchRequest,
	            @PathVariable Long companyId) {
	    	logger.info("searchUsers::::::::::::::::::::::{}"+searchRequest);
	        try {
	        	if(companyId!=null && companyId!=0) {
	        		searchRequest.setCompanyId(companyId);
	        		searchRequest.setExcludeCompanyUsers(true);
	        	}
	            PaginationResponse<UserDto> response = userService.searchUsers(searchRequest);
	            return ResponseEntity.ok(CustomApiResponse.success("Users retrieved successfully", response, HttpStatus.OK));
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(CustomApiResponse.failure("Failed to search users: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
	        }
	    }
	 	
	 	@PostMapping("/company/{companyId}/users/assign")
	 	public ResponseEntity<CustomApiResponse<String>> assignUsersToCompany(
	 	        @RequestBody AssignUsersRequest assignRequest,
	 	        @PathVariable Long companyId) {
	 	    logger.info("Assigning users to company: {}, userIds: {}", companyId, assignRequest.getUserIds());
	 	    
	 	    try {
	 	        userService.assignUsersToCompany(assignRequest.getUserIds(), companyId);
	 	        return ResponseEntity.ok(CustomApiResponse.success("Users assigned to company successfully","", HttpStatus.OK));
	 	    } catch (RuntimeException e) {
	 	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	 	                .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
	 	    } catch (Exception e) {
	 	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	 	                .body(CustomApiResponse.failure("Failed to assign users: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
	 	    }
	 	}
	 	
	 	@PostMapping("/company/search")
	    public ResponseEntity<CustomApiResponse<PaginationResponse<EmployerDTO>>> searchCompany(
	            @RequestBody CompanySearchRequest searchRequest) {
	    	logger.info("searchUsers::::::::::::::::::::::{}"+searchRequest);
	        try {
	            PaginationResponse<EmployerDTO> response = companyService.searchUsers(searchRequest);
	            return ResponseEntity.ok(CustomApiResponse.success("Companies retrieved successfully", response, HttpStatus.OK));
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(CustomApiResponse.failure("Failed to search Companies: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
	        }
	    }
	 	@GetMapping("/company/{id}")
	    public ResponseEntity<CustomApiResponse<CompanyDetailsDTO>> getCompanyById(@PathVariable Long id) {
	 		logger.info("Fetching company with ID: {}", id);
	        
	        try {
	            CompanyDetailsDTO company = companyService.getCompanyById(id);
	            return ResponseEntity.ok(CustomApiResponse.success(
	                "Company retrieved successfully", 
	                company, 
	                HttpStatus.OK
	            ));
	        } catch (RuntimeException e) {
	        	logger.warn("Company not found with ID: {}", id);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
	        } catch (Exception e) {
	        	logger.error("Error fetching company with ID {}: {}", id, e.getMessage(), e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(CustomApiResponse.failure(
	                        "Failed to fetch company: " + e.getMessage(), 
	                        HttpStatus.INTERNAL_SERVER_ERROR
	                    ));
	        }
	    }
	 	
	 	@PutMapping("/company/{id}")
	    public ResponseEntity<CustomApiResponse<CompanyRegistrationResponse>> registerCompany(
	    		@RequestBody CompanyRegistrationRequestDTO request) {
	        
	        logger.info("Received company update request for: {}", request.getCompanyName());
	        
	        try {
	            CompanyRegistrationResponse response = companyService.updateCompany(request);
	            return ResponseEntity.ok(CustomApiResponse.success(
	                "Company updated successfully", 
	                response, 
	                HttpStatus.CREATED
	            ));
	            
	        } catch (IllegalArgumentException e) {
	        	logger.warn("Validation error in company update: {}", e.getMessage());
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
	                    
	        } catch (Exception e) {
	        	logger.error("Unexpected error during company update: {}", e.getMessage(), e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(CustomApiResponse.failure(
	                        "Internal server error during update", 
	                        HttpStatus.INTERNAL_SERVER_ERROR
	                    ));
	        }
	    }
	 	
	 	 @PostMapping("/{userId}/change-password")
	     @Operation(summary = "Change user password", description = "Change password for a specific user")
	     @ApiResponses({
	         @ApiResponse(responseCode = "200", description = "Password changed successfully"),
	         @ApiResponse(responseCode = "400", description = "Invalid input or current password incorrect"),
	         @ApiResponse(responseCode = "404", description = "User not found"),
	         @ApiResponse(responseCode = "500", description = "Internal server error")
	     })
	     public ResponseEntity<CustomApiResponse<Void>> changePassword(
	             @Parameter(description = "ID of the user", required = true, example = "123")
	             @PathVariable Long userId,
	             @Valid @RequestBody ChangePasswordRequest request) {
	         try {
	             userService.changePasswordByAdmin(userId, request);
	             return ResponseEntity.ok(CustomApiResponse.success("Password changed successfully", null, HttpStatus.OK));
	         } catch (RuntimeException e) {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                     .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
	         } catch (Exception e) {
	             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                     .body(CustomApiResponse.failure("Failed to change password: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
	         }
	     }
	 	
}
