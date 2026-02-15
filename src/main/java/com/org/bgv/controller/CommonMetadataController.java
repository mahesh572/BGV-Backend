package com.org.bgv.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.dto.DegreeTypeResponse;
import com.org.bgv.dto.FieldOfStudyResponse;
import com.org.bgv.service.DocumentService;
import com.org.bgv.service.EducationService;
import com.org.bgv.service.ProfileAddressService;
import com.org.bgv.service.ProfileService;
import com.org.bgv.service.WorkExperienceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/common/metadata")
@RequiredArgsConstructor
public class CommonMetadataController {

	 private final EducationService educationHistoryService;

    @GetMapping("/degreetypes/all")
    public ResponseEntity<CustomApiResponse<List<DegreeTypeResponse>>> getAllDegreeTypes() {
        try {
            List<DegreeTypeResponse> response = educationHistoryService.getAllDegreeTypes();
            return ResponseEntity.ok(CustomApiResponse.success("Degree types retrieved successfully", response, HttpStatus.OK));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve degree types: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/fieldofstudy/all")
    public ResponseEntity<CustomApiResponse<List<FieldOfStudyResponse>>> getAllFieldsOfStudy() {
        try {
            List<FieldOfStudyResponse> response = educationHistoryService.getAllFieldsOfStudy();
            return ResponseEntity.ok(CustomApiResponse.success("Fields of study retrieved successfully", response, HttpStatus.OK));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve fields of study: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
	
	
}
