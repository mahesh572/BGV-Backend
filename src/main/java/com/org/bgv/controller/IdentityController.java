package com.org.bgv.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.org.bgv.dto.IdentitySectionRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/candidate/{candidateId}/identity")
@RequiredArgsConstructor
public class IdentityController {
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadDocuments(@ModelAttribute IdentitySectionRequest request) {
	    // Handle validation, persist details, file storage, etc.
	    return ResponseEntity.ok("Documents processed");
	}
	

	
	
	
	
}
