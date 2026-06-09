package com.org.bgv.vendor.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldVisitDto {

    private Long assignmentId;

    private Long fieldAgentId;

    private String fieldAgentName;

    private String fieldAgentEmail;

    private String fieldAgentPhone;

    private LocalDateTime assignedAt;

    private LocalDate scheduledDate;

    private String remarks;
}
