package com.org.bgv.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SavedDocumentDTO {
    private Long docId;
    private String url;
    private String status; // "uploaded", "pending-review", etc.
}