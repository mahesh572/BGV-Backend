package com.org.bgv.controller;


import com.org.bgv.api.response.ApiResponse;
import com.org.bgv.entity.Role;
import com.org.bgv.role.dto.RoleCreateRequest;
import com.org.bgv.role.dto.RoleDto;
import com.org.bgv.role.dto.RoleResponse;
import com.org.bgv.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoleDto>> createRole(@Valid @RequestBody RoleCreateRequest request) {
        try {
            RoleDto role = roleService.createRole(request);
            return ResponseEntity.ok(ApiResponse.success("Role created successfully", role, HttpStatus.CREATED));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to create role: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/by-name")
    public ResponseEntity<ApiResponse<RoleDto>> getRoleByName(@RequestParam String name) {
        try {
        	RoleDto role = roleService.getRoleByName(name)
                    .orElseThrow(() -> new RuntimeException("Role not found with name: " + name));
            return ResponseEntity.ok(ApiResponse.success("Role found", role, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to fetch role by name: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        try {
        	List<RoleResponse> roles = roleService.getAllRolesGroupedByType();
            String message = roles.isEmpty() ? "No roles found" : "Roles retrieved successfully";
            return ResponseEntity.ok(ApiResponse.success(message, roles, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to fetch roles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDto>> getRoleById(@PathVariable Long id) {
        try {
        	RoleDto role = roleService.getRoleById(id)
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
            return ResponseEntity.ok(ApiResponse.success("Role found", role, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to fetch role by id: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/by-type")
    public ResponseEntity<ApiResponse<RoleResponse>> getRolesByType(@RequestParam String roleType) {
        try {
            RoleResponse response = roleService.getRolesByType(roleType);
            String message = response.getRoles().isEmpty() ? 
                "No roles found for type: " + roleType : 
                "Roles retrieved successfully for type: " + roleType;
            return ResponseEntity.ok(ApiResponse.success(message, response, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to fetch roles by type: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDto>> updateRole(@PathVariable Long id, 
                                                       @Valid @RequestBody RoleCreateRequest request) {
        try {
        	RoleDto role = roleService.updateRole(id, request);
            return ResponseEntity.ok(ApiResponse.success("Role updated successfully", role, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to update role: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok(ApiResponse.success("Role deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to delete role: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}