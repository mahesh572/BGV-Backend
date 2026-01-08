package com.org.bgv.entity;

import java.time.LocalDateTime;

import com.org.bgv.common.DocumentStatus;

public interface BaseDocument {
	Long getDocId();
    CheckCategory getCategory();
    DocumentType getDocTypeId();
    String getFileUrl();
    Long getFileSize();
    DocumentStatus getStatus();
    LocalDateTime getUploadedAt();
    LocalDateTime getVerifiedAt();
    String getComments();
    String getAwsDocKey();
    
}
