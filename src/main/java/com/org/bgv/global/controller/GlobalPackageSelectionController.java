package com.org.bgv.global.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.bgvpackage.service.PackageSelectionConfigurationService;
import com.org.bgv.company.dto.AssignCasePreviewResponseDTO;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.controller.PackageController;
import com.org.bgv.service.PackageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user/packages")
@RequiredArgsConstructor
public class GlobalPackageSelectionController {
	
	private final PackageSelectionConfigurationService packageSelectionConfigurationService;
	
	@GetMapping("/{packageId}/preview")
    public ResponseEntity<CustomApiResponse<AssignCasePreviewResponseDTO>> getPackageforSelection(
            @PathVariable Long packageId) {
        
        try {
            log.info("GlobalPackageSelectionController for packageId: {}", packageId);
            
            
            Long companyId = SecurityUtils.getCurrentUserCompanyId();
            
            AssignCasePreviewResponseDTO result = null;
            
            result = packageSelectionConfigurationService.getPackageConfigurationSelectionforSelfRegistered(packageId);
            		
            		// assignCaseService.buildPreview(employerpackageId,companyId);
            
            return ResponseEntity.ok(
                    CustomApiResponse.<AssignCasePreviewResponseDTO>success(
                            "Package documents retrieved successfully",
                            result,
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get documents for packageId: {}", packageId, e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.<AssignCasePreviewResponseDTO>failure(
                            "Failed to retrieve package documents: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
	

}
