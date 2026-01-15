package com.org.bgv.vendor.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.org.bgv.common.DocumentStatus;
import com.org.bgv.dto.document.FileDTO;
import com.org.bgv.vendor.action.dto.ActionDTO;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VerificationFileDTO {
	
	 @JsonProperty("docId")
	 private Long docId;
	 
	private Long fileId;
	private String fileName;
	private String fileUrl;
	private Long fileSize;
	// private LocalDateTime uploadedAt;
	private DocumentStatus status;
	private String fileType;
	private String thumbnailUrl;
	private String fileKey;
	
	@JsonProperty("uploadedBy")
    private String uploadedBy;
    
    @JsonProperty("uploadedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime uploadedAt;
    
    private List<ActionDTO> actions;
    
    private String size;
    
    private String mimeType;
    private String verificationNotes;
    private String comments;
    private boolean verified;
    private LocalDateTime verifiedAt;
    private String verifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for verification case document info
    private Boolean isAddOn;
    private Boolean required;
    private Double documentPrice;
    private String verificationStatus;
}
