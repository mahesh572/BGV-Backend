package com.org.bgv.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentUploadRequest {
	
	
	    private String type;  // AADHAR
        private String label; // display name
        private Long typeId;
	    private List<FieldDTO> fields;

	   // private UploadRuleDTO upload;

	   // private List<SavedDocumentDTO> savedDocuments;

	   // private List<MultipartFile> filesToAdd;

	  //  private List<FileToDeleteDTO> filesToDelete;
}
