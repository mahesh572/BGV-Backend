package com.org.bgv.vendor.action.dto;

import com.org.bgv.vendor.dto.EvidenceSource;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvidenceLinkRequest {
    private Long evidenceId;
    private EvidenceSource source;
    private Long documentId;
}
