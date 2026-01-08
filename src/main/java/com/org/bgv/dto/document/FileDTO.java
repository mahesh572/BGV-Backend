package com.org.bgv.dto.document;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.org.bgv.common.DocumentStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FileDTO {
	
	private Long fileId;
	private String fileName;
	private String fileUrl;
	private Long fileSize;
	// private LocalDateTime uploadedAt;
	private DocumentStatus status;
	private String fileType;
	private String thumbnailUrl;
	
	@JsonProperty("uploadedBy")
    private String uploadedBy;
    
    @JsonProperty("uploadedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime uploadedAt;
}
