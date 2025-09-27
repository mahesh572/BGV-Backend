package com.org.bgv.entity;

import java.time.LocalDateTime;

public interface BaseDocument {
	Long getDoc_id();
    DocumentCategory getCategory();
    DocumentType getType_id();
    String getFile_url();
    Long getFile_size();
    String getStatus();
    LocalDateTime getUploadedAt();
    LocalDateTime getVerifiedAt();
    String getComments();
    String getAwsDocKey();
    
}
