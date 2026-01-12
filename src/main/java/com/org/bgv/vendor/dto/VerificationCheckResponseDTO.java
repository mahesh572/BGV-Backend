package com.org.bgv.vendor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.org.bgv.common.DocumentTypeInfo;
import com.org.bgv.vendor.action.dto.ActionDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationCheckResponseDTO {
    
    @JsonProperty("caseId")
    private String caseId;
    
    @JsonProperty("caseRef")
    private String caseRef;
    
    @JsonProperty("checkId")
    private String checkId;
    
    @JsonProperty("checkRef")
    private String checkRef;
    
    @JsonProperty("checkType")
    private String checkType;
    
    @JsonProperty("checkName")
    private String checkName;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("candidate")
    private CandidateInfoDTO candidate;
    
    private List<ActionDTO> actions;
    
    @JsonProperty("objects")
    private List<ObjectDTO> objects;
    
    @JsonProperty("documentTypeInfos")
    private List<DocumentTypeInfo> documentTypeInfos;
    
    @JsonProperty("evidenceTypeList")
    private List<EvidenceTypeDTO> evidenceTypeList;
    
    private EmployerInfoDTO employer;
    
    private List<TimelineEventDTO> timeline;
    private List<RequirementDTO> requirements;
    private SlaInfoDTO slas;
    private List<VendorNoteDTO> vendorNotes;
    private List<EvidenceDTO> evidence;
    private List<VerificationHistoryDTO> verificationHistory;
    
    // Type-specific declared information (different for each check type)
    private Map<String, Object> declaredInfo;
    
    // Current check-specific context (different for each check type)
    private Map<String, Object> context;
    
    @JsonProperty("audit")
    private AuditDTO audit;
}
