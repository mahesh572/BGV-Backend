package com.org.bgv.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DocumentResponse {
	private Long doc_id;
    private Long category_id;
    private Long doc_type_id;
    private String file_url;
    private Long file_size;
    private String status;
    private LocalDateTime uploadedAt;
    private LocalDateTime verifiedAt;
    private String comments;
    private String awsDocKey;
    private Long entity_id;
    private String file_name;
    private String file_type;
    private String document_type_name;
    private String category_name;
    
}


