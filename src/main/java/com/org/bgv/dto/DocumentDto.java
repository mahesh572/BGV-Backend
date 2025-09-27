package com.org.bgv.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentDto {
    
	private Long doc_id;
    private String fileUrl;
    private Boolean isNew;
    private BigDecimal size;
    private String status;
   
	private Long userId;
	private Long documentTypeId;
	private Long categoryId;
	private Long entity_id;
}