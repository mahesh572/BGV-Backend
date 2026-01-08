package com.org.bgv.vendor.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorNoteDTO {
    private String id;
    private String content;
    private String createdBy;
    private LocalDateTime createdAt;
    private String type; // internal, verification, insufficiency
    private boolean isInternal;
}
