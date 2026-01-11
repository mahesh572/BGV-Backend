package com.org.bgv.vendor.evidence.dto;
import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
public class EvidenceUploadResponse {
    private Long evidenceId;
    private String fileKey;      // storageKey
    private String documentId;   // For compatibility with frontend
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String fileUrl;
    private LocalDateTime uploadedAt;
}
