// PackageController.java
package com.org.bgv.controller;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.PackageDTO;
import com.org.bgv.common.PackageRequest;
import com.org.bgv.service.PackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @PostMapping
    public ResponseEntity<CustomApiResponse<PackageDTO>> createPackage(@Valid @RequestBody PackageRequest request) {
        try {
            PackageDTO createdPackage = packageService.createPackage(request);
            return ResponseEntity.ok(CustomApiResponse.success("Package created successfully", createdPackage, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to create package: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<PackageDTO>> getPackageById(@PathVariable Long id) {
        try {
            PackageDTO packageDTO = packageService.getPackageById(id);
            return ResponseEntity.ok(CustomApiResponse.success("Package retrieved successfully", packageDTO, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve package: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping
    public ResponseEntity<CustomApiResponse<List<PackageDTO>>> getAllPackages() {
        try {
            List<PackageDTO> packages = packageService.getAllPackages();
            return ResponseEntity.ok(CustomApiResponse.success("Packages retrieved successfully", packages, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve packages: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<CustomApiResponse<List<PackageDTO>>> getActivePackages() {
        try {
            List<PackageDTO> packages = packageService.getActivePackages();
            return ResponseEntity.ok(CustomApiResponse.success("Active packages retrieved successfully", packages, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve active packages: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse<PackageDTO>> updatePackage(
            @PathVariable Long id, 
            @Valid @RequestBody PackageRequest request) {
        try {
            PackageDTO updatedPackage = packageService.updatePackage(id, request);
            return ResponseEntity.ok(CustomApiResponse.success("Package updated successfully", updatedPackage, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update package: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse<Void>> deletePackage(@PathVariable Long id) {
        try {
            packageService.deletePackage(id);
            return ResponseEntity.ok(CustomApiResponse.success("Package deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete package: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomApiResponse<PackageDTO>> togglePackageStatus(
            @PathVariable Long id, 
            @RequestParam Boolean isActive) {
        try {
            PackageDTO updatedPackage = packageService.togglePackageStatus(id, isActive);
            String message = isActive ? "Package activated successfully" : "Package deactivated successfully";
            return ResponseEntity.ok(CustomApiResponse.success(message, updatedPackage, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update package status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}