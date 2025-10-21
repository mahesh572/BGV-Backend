package com.org.bgv.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.config.JwtUtil;
import com.org.bgv.dto.VendorDTO;
import com.org.bgv.service.UserService;
import com.org.bgv.service.VendorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vendors")
@CrossOrigin(origins = "*") // Adjust based on your frontend URL
@RequiredArgsConstructor
public class VendorController {

	private final VendorService vendorService;
	
	@PostMapping
    public ResponseEntity<CustomApiResponse<Boolean>> createVendor(
            @RequestBody VendorDTO vendorRequestDTO) {
        try {
        	System.out.println("vendorRequestDTO::::::::::::::::::::::{}"+vendorRequestDTO);
            Boolean isSuccess = vendorService.createVendor(vendorRequestDTO);
                        
            return ResponseEntity.ok(CustomApiResponse.success("Vendor created successfully", isSuccess, HttpStatus.OK));
        } catch (Exception e) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to fetch users: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
	
}
