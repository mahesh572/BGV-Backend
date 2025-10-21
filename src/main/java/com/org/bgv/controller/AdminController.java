package com.org.bgv.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.ChangePasswordRequest;
import com.org.bgv.common.UserDto;
import com.org.bgv.dto.BasicdetailsDTO;
import com.org.bgv.dto.UserDetailsDto;
import com.org.bgv.mapper.UserMapper;
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
	
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	 	@PostMapping
	    public ResponseEntity<CustomApiResponse<UserDto>> create(@RequestBody UserDto userDto) {
	        try {
	        	logger.info("users/create::::::{}",userDto);
	        //	userDto.setUserType(Constants.USER_TYPE_CANDIDATE);
	        	UserDto createdUser = userService.create(userDto);
	        	logger.info("AdminController:::::createdUser::{}",createdUser);
	        	// Profile creation
	        	BasicdetailsDTO basicDetailsDTO = userMapper.mapUserDTOToBasicdetails(createdUser);
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
