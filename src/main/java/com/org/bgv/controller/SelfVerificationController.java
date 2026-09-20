package com.org.bgv.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.company.dto.AssignCasePreviewResponseDTO;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/selfverification")
@Slf4j
public class SelfVerificationController {
	
	
	@GetMapping("/preview")
    public void getSelfPackageDocuments() {
		
		
		
        
	}

}
