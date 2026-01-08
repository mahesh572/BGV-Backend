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
public class VerificationHistoryDTO {
    private String id;
    private String action;
    private String fromStatus;
    private String toStatus;
    private String performedBy;
    private LocalDateTime timestamp;
    private String notes;
}
