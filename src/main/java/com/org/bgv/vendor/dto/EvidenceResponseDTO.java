package com.org.bgv.vendor.dto;

import java.time.LocalDateTime;

import com.org.bgv.common.EvidenceLevel;
import com.org.bgv.common.EvidenceStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvidenceResponseDTO {
    private Long evidenceId;
    private EvidenceLevel level;
    private String fileName;
    private String fileUrl;
    private EvidenceStatus status;
    private Long objectId;
    private Long documentTypeId;
    private Long docId;
    private LocalDateTime uploadedAt;
}

