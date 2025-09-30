package com.org.bgv.dto.document;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FileDTO {
	
	private Long fileId;
	private String fileName;
	private String fileUrl;
	private Long fileSize;
	private LocalDateTime uploadedAt;
	private String status;
	private String fileType;
	private String thumbnailUrl;
	
}
