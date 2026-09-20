package com.org.bgv.service;

import org.springframework.stereotype.Service;

import com.org.bgv.company.dto.AssignCasePreviewResponseDTO;

@Service
public class SelfCasePreviewService {
	
	    
	    public AssignCasePreviewResponseDTO generatePreview(Long studentId) {

	    	AssignCasePreviewResponseDTO assignCasePreviewResponseDTO = AssignCasePreviewResponseDTO.builder().build();
	    	
	    	return assignCasePreviewResponseDTO;
		 
	 }

}
