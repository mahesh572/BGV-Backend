package com.org.bgv.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IdentitySectionRequest {

	private String section;  // Identity
    private String label;   //  Identity
    private List<DocumentUploadRequest> documents;
	
}
