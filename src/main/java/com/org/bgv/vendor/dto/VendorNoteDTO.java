package com.org.bgv.vendor.dto;

import java.time.LocalDateTime;

import com.org.bgv.enums.VendorNoteType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorNoteDTO {

    private Long noteId;

    private String content;

    private String createdBy;

    private LocalDateTime createdAt;

    private VendorNoteType type;

    private Boolean visibleToEmployer;

    private Boolean visibleToCandidate;

    private boolean internal;
}