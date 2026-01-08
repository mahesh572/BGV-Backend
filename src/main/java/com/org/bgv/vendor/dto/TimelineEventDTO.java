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
public class TimelineEventDTO {
    private String id;
    private String action;
    private String description;
    private String performedBy;
    private LocalDateTime timestamp;
    private String icon;
}