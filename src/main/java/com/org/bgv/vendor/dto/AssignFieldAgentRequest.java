package com.org.bgv.vendor.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssignFieldAgentRequest {

    private Long agentUserId;

    private LocalDate scheduledDate;

    private String remarks;
}
