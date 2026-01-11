package com.org.bgv.vendor.action.dto;

import java.util.List;

import com.org.bgv.vendor.dto.ActionLevel;
import com.org.bgv.vendor.dto.ActionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationActionRequest {

    private ActionType actionType;
    private ActionLevel actionLevel;

    private Long caseId;
    private Long checkId;
    private Long objectId;
    private Long documentId;

    private Long reasonId;
    private String remarks;

    private List<EvidenceLinkRequest> evidences;
}
