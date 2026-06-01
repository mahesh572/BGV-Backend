package com.org.bgv.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.s3.S3StorageService;
import com.org.bgv.vendor.dto.VerificationMethodDTO;
import com.org.bgv.vendor.service.VerificationActionService;
import com.org.bgv.vendor.service.VerificationMethodService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/vendor/methods")
@RequiredArgsConstructor
@Slf4j
public class VerificationMethodsController {

	private final VerificationMethodService verificationMethodService;
	
	@GetMapping("/checks/{checkType}/verification-methods")
	public List<VerificationMethodDTO> getMethods(
	        @PathVariable CheckCategoryEnum checkType) {

	    return verificationMethodService
	            .getMethodsForCheck(checkType);
	}
	
	
}
