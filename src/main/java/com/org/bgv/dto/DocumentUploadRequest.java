package com.org.bgv.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.org.bgv.dto.document.FileDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentUploadRequest {
	
	    private Long id;
	    private String type;  // AADHAR
        private String label; // display name
        private Long typeId;
	    private List<FieldDTO> fields;
	    private List<FileDTO> files;
	    private Integer maxfiles;
	    private String typeLabel;

	   // private UploadRuleDTO upload;

	   // private List<SavedDocumentDTO> savedDocuments;

	   // private List<MultipartFile> filesToAdd;

	  //  private List<FileToDeleteDTO> filesToDelete;
}
