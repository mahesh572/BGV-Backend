package com.org.bgv.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentDto {
    
	private Long doc_id;
	
	 @NotNull(message = "Candidate ID is required")
	 private Long candidateId;
	
	private String fileName;
	private String originalFileName; 
    private String fileUrl;
    private String fileType; // pdf, jpg, png, doc
    private Boolean isNew;
    private BigDecimal size;
    private String status;
   
	private Long userId;
	private Long documentTypeId;
	private Long categoryId;
	private Long entity_id;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime uploadedAt;
    
    private String uploadedBy;
    
    private boolean verified;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime verifiedAt;
    
    private String verifiedBy;
    private String verificationNotes;
    
 // For response
    private String downloadUrl;
    private String previewUrl;
    
}