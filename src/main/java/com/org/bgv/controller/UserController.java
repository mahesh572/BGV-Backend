package com.org.bgv.controller;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.PageRequestDto;
import com.org.bgv.common.PaginationResponse;
import com.org.bgv.common.UserDto;
import com.org.bgv.common.UserSearchRequest;
import com.org.bgv.config.JwtUtil;
import com.org.bgv.constants.Constants;
import com.org.bgv.constants.UserStatus;
import com.org.bgv.dto.UserDetailsDto;
import com.org.bgv.service.UserService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /*
    @GetMapping
    public ResponseEntity<CustomApiResponse<PaginationResponse<UserDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        try {
            PageRequestDto pageRequest = PageRequestDto.builder()
                    .page(page)
                    .size(size)
                    .sortBy(sortBy)
                    .sortDirection(sortDirection)
                    .build();
            
            PaginationResponse<UserDto> response = userService.getAllUsers(pageRequest);
            return ResponseEntity.ok(CustomApiResponse.success("Users retrieved successfully", response, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch users: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    */
    
   
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<UserDto>> getById(@PathVariable Long id) {
        try {
        	UserDto user = userService.getById(id);
            return ResponseEntity.ok(CustomApiResponse.success("User found", user, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping
    public ResponseEntity<CustomApiResponse<UserDto>> create(@RequestBody UserDto userDto) {
        try {
        	logger.info("users/create::::::{}",userDto);
        	userDto.setUserType(Constants.USER_TYPE_CANDIDATE);
        	UserDto createdUser = userService.create(userDto);
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

    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse<UserDto>> update(@PathVariable Long id, @RequestBody UserDetailsDto userDto) {
        try {
        	UserDto updatedUser = userService.update(id, userDto);
            return ResponseEntity.ok(CustomApiResponse.success("User updated successfully", updatedUser, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok(CustomApiResponse.success("User deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/by-email")
    public ResponseEntity<CustomApiResponse<UserDto>> getUserByEmail(@RequestParam String email) {
        try {
        	UserDto userDto = userService.getUserByEmail(email);
            return ResponseEntity.ok(CustomApiResponse.success("User found", userDto, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch user by email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<CustomApiResponse<Void>> assignRole(@PathVariable Long userId, @PathVariable String roleName) {
        try {
            userService.assignRoleToUser(userId, roleName);
            return ResponseEntity.ok(CustomApiResponse.success("Role assigned successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to assign role: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @GetMapping("/user-from-token")
    public ResponseEntity<CustomApiResponse<UserDto>> getUserFromToken(
            @RequestHeader("Authorization") String authorizationHeader) {
    	logger.info("getUserFromToken:::::::::::::::::::::::::::{}",authorizationHeader);
        try {
            // Extract token from "Bearer <token>" format
            String token = jwtUtil.extractTokenFromHeader(authorizationHeader);
            
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(CustomApiResponse.failure("Authorization token is required", HttpStatus.BAD_REQUEST));
            }

            UserDto userDto = userService.getUserFromToken(token);
            return ResponseEntity.ok(CustomApiResponse.success("User retrieved successfully", userDto, HttpStatus.OK));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to get user from token: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}