package com.org.bgv.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.org.bgv.common.DocumentTypeInfo;
import com.org.bgv.entity.VerificationCaseDocument;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorVerificationCheckDTO {
    // Common fields for all verification types
    private Long checkId;
    private String checkRef;
    private String caseId;
    private String caseRef;
    private String checkType; // education, employment, identity, criminal, reference
    private String status;
    private String categoryName;
    private String categoryCode;
    
    // Common timeline, requirements, and SLA
    private CandidateInfoDTO candidate;
    private List<VerificationDocumentDTO> documents;
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
    
    // Employer information (for context)
    private EmployerInfoDTO employer;
    
    
    // for evidence section
    
    private List<EvidenceTypeDTO> evidenceTypeList;
    private List<DocumentTypeInfo> documentTypeInfos;
}