package com.org.bgv.entity;

import java.time.LocalDateTime;

public interface BaseDocument {
	Long getDocId();
    DocumentCategory getCategory();
    DocumentType getDocTypeId();
    String getFileUrl();
    Long getFileSize();
    String getStatus();
    LocalDateTime getUploadedAt();
    LocalDateTime getVerifiedAt();
    String getComments();
    String getAwsDocKey();
    
}
