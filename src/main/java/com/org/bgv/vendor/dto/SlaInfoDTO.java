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
public class SlaInfoDTO {
    private LocalDateTime assignedDate;
    private LocalDateTime dueDate;
    private Long daysRemaining;
    private String status; // on_track, at_risk, breached
    private LocalDateTime completedAt;
}