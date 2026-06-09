package com.org.bgv.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.commom.dto.StateRegionDto;
import com.org.bgv.commom.dto.VerificationServiceResponse;
import com.org.bgv.common.CheckCategoryResponse;
import com.org.bgv.common.RoleConstants;
import com.org.bgv.common.entity.StateRegion;
import com.org.bgv.common.service.LocationService;
import com.org.bgv.config.JwtUtil;
import com.org.bgv.dto.CreateVendorUserRequest;
import com.org.bgv.role.dto.RoleDetailDto;
import com.org.bgv.role.dto.RoleResponse;
import com.org.bgv.service.CheckCategoryService;
import com.org.bgv.service.RoleService;
import com.org.bgv.service.UserService;
import com.org.bgv.service.VendorService;
import com.org.bgv.vendor.dto.VendorUserMetaInfoResponse;
import com.org.bgv.vendor.entity.VendorUser;
import com.org.bgv.vendor.service.VendorUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vendor/users")
@CrossOrigin(origins = "*") // Adjust based on your frontend URL
@RequiredArgsConstructor
public class VendorUserController {

//	private final VendorService vendorService;
	private final VendorUserService vendorUserService;
	
	private final RoleService roleService;
	private final LocationService locationService;
	private final CheckCategoryService checkCategoryService;
	
	@PostMapping
    public ResponseEntity<CustomApiResponse<?>> createVendor(
            @RequestBody CreateVendorUserRequest vendorRequestDTO) {
        try {
        	System.out.println("vendorRequestDTO::::::::::::::::::::::{}"+vendorRequestDTO);
            VendorUser isSuccess = vendorUserService.createVendorUser(vendorRequestDTO);
                        
            return ResponseEntity.ok(CustomApiResponse.success("Vendor created successfully", isSuccess, HttpStatus.OK));
        } catch (Exception e) {
        	e.printStackTrace();
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch users: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
	
	@GetMapping("/metaInfo")
	public ResponseEntity<CustomApiResponse<VendorUserMetaInfoResponse>> vendorUserMetaInfo() {

		List<RoleDetailDto> roleResponse =
	            roleService.getRolesByTypeMetaInfo(RoleConstants.TYPE_VENDOR_LABEL);

	    List<StateRegionDto> regions =
	            locationService.getStatesByCountryCode("IN");

	    List<VerificationServiceResponse> servicesProvided =
	            checkCategoryService.getVerificationServices();

	    VendorUserMetaInfoResponse response =
	            VendorUserMetaInfoResponse.builder()
	                    .roles(roleResponse)
	                    .regions(regions)
	                    .servicesProvided(servicesProvided)
	                    .build();

	    return ResponseEntity.ok(
	            CustomApiResponse.success(
	                    "Vendor meta information fetched successfully",
	                    response,
	                    HttpStatus.OK));
	}
	
	
	
}
