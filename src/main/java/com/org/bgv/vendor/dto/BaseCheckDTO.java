package com.org.bgv.vendor.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseCheckDTO {
    private String checkId;
    private String caseId;
    private String caseRef;
    private String checkType;
    private String status;
    private CandidateInfoDTO candidate;
    private List<VerificationDocumentDTO> documents;
    private List<TimelineEventDTO> timeline;
    private List<RequirementDTO> requirements;
    private SlaInfoDTO slas;
    private List<VendorNoteDTO> vendorNotes;
    private List<EvidenceDTO> evidence;
    private List<VerificationHistoryDTO> verificationHistory;
    private EmployerInfoDTO employer;
}
