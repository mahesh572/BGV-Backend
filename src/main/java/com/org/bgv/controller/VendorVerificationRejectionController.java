package com.org.bgv.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.mapper.UserMapper;
import com.org.bgv.service.CompanyService;
import com.org.bgv.service.ProfileService;
import com.org.bgv.service.UserService;
import com.org.bgv.vendor.dto.CreateRejectionRequest;
// import com.org.bgv.vendor.service.RejectionService;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
/*
@RestController
@RequestMapping("/api/vendor/rejections")
@RequiredArgsConstructor
public class VendorVerificationRejectionController {

	private final RejectionService rejectionService;
	
	@PostMapping
	public ResponseEntity<?> reject(
	        @RequestBody CreateRejectionRequest request,
	        @RequestParam Long vendorId
	) {
	    rejectionService.reject(request, vendorId);
	    return ResponseEntity.ok().build();
	}
	
	@GetMapping
	public ResponseEntity<?> getRejections(
	        @RequestParam Long checkId
	) {
	    rejectionService.getRejections(checkId);
	    return ResponseEntity.ok().build();
	}
	
	@PutMapping("/{id}/resolve")
	public ResponseEntity<?> resolve(
	        @PathVariable Long id,
	        @RequestParam Long vendorId
	) {
	    rejectionService.resolve(id, vendorId);
	    return ResponseEntity.ok().build();
	}
	
}

*/
