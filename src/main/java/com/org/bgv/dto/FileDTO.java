package com.org.bgv.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FileDTO {
	
	private Long id;
	private Long userId;
	private Long typeId;
	private Long categoryId;
	
}
