package com.org.bgv.controller;


import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.UserRoleUpdateRequest;
import com.org.bgv.role.dto.RoleCreateRequest;
import com.org.bgv.role.dto.RoleDto;
import com.org.bgv.role.dto.RoleResponse;
import com.org.bgv.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "APIs for managing user roles and permissions")
@SecurityRequirement(name = "Bearer Authentication")
public class RoleController {

	private static final Logger log = LoggerFactory.getLogger(RoleController.class);
    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Create a new role", description = "Creates a new role with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Role created successfully", 
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or role already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomApiResponse<RoleDto>> createRole(@Valid @RequestBody RoleCreateRequest request) {
        try {
            RoleDto role = roleService.createRole(request);
            return ResponseEntity.ok(CustomApiResponse.success("Role created successfully", role, HttpStatus.CREATED));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to create role: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/by-name")
    @Operation(summary = "Get role by name", description = "Retrieves a role by its name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role found successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomApiResponse<RoleDto>> getRoleByName(
            @Parameter(description = "Name of the role to retrieve", required = true, example = "Administrator")
            @RequestParam String name) {
        try {
            RoleDto role = roleService.getRoleByName(name)
                    .orElseThrow(() -> new RuntimeException("Role not found with name: " + name));
            return ResponseEntity.ok(CustomApiResponse.success("Role found", role, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch role by name: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping
    @Operation(summary = "Get all roles", description = "Retrieves all roles grouped by type")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Roles retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomApiResponse<List<RoleDto>>> getAllRoleswithoutGroup() {
        try {
            List<RoleDto> roles = roleService.getAllRoles();
            String message = roles.isEmpty() ? "No roles found" : "Roles retrieved successfully";
            return ResponseEntity.ok(CustomApiResponse.success(message, roles, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch roles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    
    @GetMapping("/group")
    @Operation(summary = "Get all roles", description = "Retrieves all roles grouped by type")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Roles retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomApiResponse<List<RoleResponse>>> getAllRoles() {
        try {
            List<RoleResponse> roles = roleService.getAllRolesGroupedByType();
            String message = roles.isEmpty() ? "No roles found" : "Roles retrieved successfully";
            return ResponseEntity.ok(CustomApiResponse.success(message, roles, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch roles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    /*
    @GetMapping("/group")
    @Operation(summary = "Get all roles", description = "Retrieves all roles grouped by type")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Roles retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomApiResponse<List<RoleResponse>>> getCompanyAllRoles() {
        try {
            List<RoleResponse> roles = roleService.getAllRolesGroupedByType();
            String message = roles.isEmpty() ? "No roles found" : "Roles retrieved successfully";
            return ResponseEntity.ok(CustomApiResponse.success(message, roles, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch roles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
*/
    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", description = "Retrieves a role by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role found successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomApiResponse<RoleDto>> getRoleById(
            @Parameter(description = "ID of the role to retrieve", required = true, example = "1")
            @PathVariable Long id) {
        try {
            RoleDto role = roleService.getRoleById(id)
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
            return ResponseEntity.ok(CustomApiResponse.success("Role found", role, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch role by id: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/by-type")
    @Operation(summary = "Get roles by type", description = "Retrieves all roles of a specific type")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Roles retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No roles found for the specified type"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomApiResponse<RoleResponse>> getRolesByType(
            @Parameter(description = "Type of roles to retrieve", required = true, example = "regular")
            @RequestParam String roleType) {
        try {
            RoleResponse response = roleService.getRolesByType(roleType);
            String message = response.getRoles().isEmpty() ? 
                "No roles found for type: " + roleType : 
                "Roles retrieved successfully for type: " + roleType;
            return ResponseEntity.ok(CustomApiResponse.success(message, response, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch roles by type: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role", description = "Updates an existing role with new details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role updated successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomApiResponse<RoleDto>> updateRole(
            @Parameter(description = "ID of the role to update", required = true, example = "1")
            @PathVariable Long id, 
            @Valid @RequestBody RoleCreateRequest request) {
        try {
            RoleDto role = roleService.updateRole(id, request);
            return ResponseEntity.ok(CustomApiResponse.success("Role updated successfully", role, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update role: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role", description = "Deletes a role by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or role in use"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomApiResponse<Void>> deleteRole(
            @Parameter(description = "ID of the role to delete", required = true, example = "1")
            @PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok(CustomApiResponse.success("Role deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete role: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get roles by user ID", description = "Retrieves all roles assigned to a specific user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User roles retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomApiResponse<List<RoleDto>>> getRolesByUserId(
            @Parameter(description = "ID of the user to retrieve roles for", required = true, example = "123")
            @PathVariable Long userId) {
        try {
            List<RoleDto> roles = roleService.getUserRoles(userId);
            return ResponseEntity.ok(CustomApiResponse.success("Roles fetched successfully", roles, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch user roles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @PostMapping("/{userId}/roles")
    @Operation(summary = "Update user roles", description = "Add and remove roles for a specific user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User roles updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or user not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomApiResponse<Void>> updateUserRoles(
            @Parameter(description = "ID of the user", required = true, example = "123")
            @PathVariable Long userId,
            @Parameter(description = "Role IDs to add and remove", required = true)
            @RequestBody UserRoleUpdateRequest request) {
        try {
        	roleService.updateUserRoles(userId, request.getRoleIdsToAdd(), request.getRoleIdsToRemove());
            return ResponseEntity.ok(CustomApiResponse.success("User roles updated successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update user roles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}