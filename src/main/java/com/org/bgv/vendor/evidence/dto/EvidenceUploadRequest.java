package com.org.bgv.vendor.evidence.dto;

import lombok.Builder;
import lombok.Data;

import com.org.bgv.vendor.dto.ActionLevel;
import com.org.bgv.vendor.dto.ActionType;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceUploadRequest {
    private String fileName;
    private String originalFileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String storageKey;
    
    private Long caseId;
    private Long checkId;
    private Long candidateId;
    private ActionType actionType;
    private ActionLevel actionLevel;
    private Long reasonId;
    private Long objectId;
    private Long docId;
    
    private Long uploadedBy;
}