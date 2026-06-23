package com.org.bgv.vendor.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.service.util.UserServiceUtil;
import com.org.bgv.vendor.dto.EvidenceResponseDTO;
import com.org.bgv.vendor.entity.VerificationActionEvidence;
import com.org.bgv.vendor.entity.VerificationMethodExecution;
import com.org.bgv.vendor.entity.VerificationMethodExecutionEvidence;
import com.org.bgv.vendor.repository.VerificationActionEvidenceRepository;
import com.org.bgv.vendor.repository.VerificationMethodExecutionEvidenceRepository;
import com.org.bgv.vendor.repository.VerificationMethodExecutionRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class EvidenceService {
	
	private final UserServiceUtil userServiceUtil;
	
	private final VerificationActionEvidenceRepository verificationActionEvidenceRepository;
	private final VerificationMethodExecutionEvidenceRepository verificationMethodExecutionEvidenceRepository;
	private final VerificationMethodExecutionRepository verificationMethodExecutionRepository;
    
    // ==================== LEVEL 1: CHECK LEVEL ====================
    /**
     * Get ALL evidences (action + method) for a specific check
     * Level: Check → All Evidences
     */
    public List<EvidenceResponseDTO> getEvidenceForCheck(Long checkId) {
        List<EvidenceResponseDTO> evidenceList = new ArrayList<>();
        
        // 1. Get evidence from VerificationActionEvidence
        List<VerificationActionEvidence> actionEvidence = 
            verificationActionEvidenceRepository.findByVerificationCaseCheck_CaseCheckId(checkId);
        
        for (VerificationActionEvidence evidence : actionEvidence) {
            EvidenceResponseDTO dto = convertActionEvidenceToDTO(evidence);
            evidenceList.add(dto);
        }
        
        // 2. Get evidence from VerificationMethodExecutionEvidence
        List<VerificationMethodExecutionEvidence> methodEvidence = 
            verificationMethodExecutionEvidenceRepository.findByVerificationCaseCheck_CaseCheckId(checkId);
        
        for (VerificationMethodExecutionEvidence evidence : methodEvidence) {
            EvidenceResponseDTO dto = convertMethodEvidenceToDTO(evidence);
            evidenceList.add(dto);
        }
        
        // Sort by uploaded date (newest first)
        evidenceList.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        
        return evidenceList;
    }

    // ==================== LEVEL 2: ACTION LEVEL ====================
    /**
     * Get evidences for a specific action
     * Level: Action → Evidences
     */
    public List<EvidenceResponseDTO> getEvidenceForAction(Long actionId) {
        List<EvidenceResponseDTO> evidenceList = new ArrayList<>();
        
        // Get action level evidences
        List<VerificationActionEvidence> actionEvidence = 
            verificationActionEvidenceRepository.findByAction_Id(actionId);
        
        for (VerificationActionEvidence evidence : actionEvidence) {
            EvidenceResponseDTO dto = convertActionEvidenceToDTO(evidence);
            evidenceList.add(dto);
        }
        
        // Get method execution evidences under this action
        List<VerificationMethodExecution> methodExecutions = 
            verificationMethodExecutionRepository.findByAction_Id(actionId);
        
        for (VerificationMethodExecution execution : methodExecutions) {
            List<VerificationMethodExecutionEvidence> methodEvidences = 
                verificationMethodExecutionEvidenceRepository
                    .findByMethodExecution_ExecutionId(execution.getExecutionId());
            
            for (VerificationMethodExecutionEvidence evidence : methodEvidences) {
                EvidenceResponseDTO dto = convertMethodEvidenceToDTO(evidence);
                evidenceList.add(dto);
            }
        }
        
        // Sort by uploaded date (newest first)
        evidenceList.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        
        return evidenceList;
    }

    // ==================== LEVEL 3: EXECUTION LEVEL ====================
    /**
     * Get evidences for a specific method execution
     * Level: Method Execution → Evidence
     */
    public List<EvidenceResponseDTO> getEvidenceForExecution(Long executionId) {
        List<EvidenceResponseDTO> evidenceList = new ArrayList<>();
        
        List<VerificationMethodExecutionEvidence> methodEvidences = 
            verificationMethodExecutionEvidenceRepository
                .findByMethodExecution_ExecutionId(executionId);
        
        for (VerificationMethodExecutionEvidence evidence : methodEvidences) {
            EvidenceResponseDTO dto = convertMethodEvidenceToDTO(evidence);
            evidenceList.add(dto);
        }
        
        // Sort by uploaded date (newest first)
        evidenceList.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        
        return evidenceList;
    }

    // ==================== LEVEL 4: OBJECT LEVEL ====================
    /**
     * Get evidences by object (e.g., EMAIL, PORTAL, HR_CONFIRMATION)
     * Level: Object → Evidence
     */
    public List<EvidenceResponseDTO> getEvidenceByObject(String objectType, Long objectId) {
        List<EvidenceResponseDTO> evidenceList = new ArrayList<>();
        
        // Get method executions for this object
        List<VerificationMethodExecution> methodExecutions = 
            verificationMethodExecutionRepository
                .findByObjectTypeAndObjectId(objectType, objectId);
       
        
        for (VerificationMethodExecution execution : methodExecutions) {
            List<VerificationMethodExecutionEvidence> methodEvidences = 
                verificationMethodExecutionEvidenceRepository
                    .findByMethodExecution_ExecutionId(execution.getExecutionId());
            
            for (VerificationMethodExecutionEvidence evidence : methodEvidences) {
                EvidenceResponseDTO dto = convertMethodEvidenceToDTO(evidence);
                // Add object information
                dto.setObjectType(objectType);
                dto.setObjectId(objectId);
                evidenceList.add(dto);
            }
        }
        
        // Sort by uploaded date (newest first)
        evidenceList.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        
        return evidenceList;
    }

    // ==================== LEVEL 5: CHECK + OBJECT LEVEL ====================
    /**
     * Get evidences by check and object
     * Level: Check → Object → Evidence
     */
    public List<EvidenceResponseDTO> getEvidenceByCheckAndObject(
            Long checkId, String objectType, Long objectId) {
        List<EvidenceResponseDTO> evidenceList = new ArrayList<>();
        
        
        List<VerificationActionEvidence> actionEvidence = 
                verificationActionEvidenceRepository.findByVerificationCaseCheck_CaseCheckIdAndObjectId(checkId,objectId);
        
        for (VerificationActionEvidence evidence : actionEvidence) {
            EvidenceResponseDTO dto = convertActionEvidenceToDTO(evidence);
            evidenceList.add(dto);
        }
        
        // Get method executions for this check and object
        List<VerificationMethodExecution> methodExecutions = 
            verificationMethodExecutionRepository
                .findByVerificationCheck_CaseCheckIdAndObjectTypeAndObjectId(
                    checkId, objectType, objectId);
        
        for (VerificationMethodExecution execution : methodExecutions) {
            List<VerificationMethodExecutionEvidence> methodEvidences = 
                verificationMethodExecutionEvidenceRepository
                    .findByMethodExecution_ExecutionId(execution.getExecutionId());
            
            for (VerificationMethodExecutionEvidence evidence : methodEvidences) {
                EvidenceResponseDTO dto = convertMethodEvidenceToDTO(evidence);
                dto.setObjectType(objectType);
                dto.setObjectId(objectId);
                evidenceList.add(dto);
            }
        }
        
        // Sort by uploaded date (newest first)
        evidenceList.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        
        return evidenceList;
    }

    // ==================== LEVEL 6: CHECK + ACTION LEVEL ====================
    /**
     * Get evidences by check and action
     * Level: Check → Action → Evidence
     */
    public List<EvidenceResponseDTO> getEvidenceByCheckAndAction(Long checkId, Long actionId) {
        List<EvidenceResponseDTO> evidenceList = new ArrayList<>();
        
        // Get action evidence
        List<VerificationActionEvidence> actionEvidences =       verificationActionEvidenceRepository.findByVerificationCaseCheck_CaseCheckIdAndAction_Id(checkId, actionId);
        
        for (VerificationActionEvidence evidence : actionEvidences) {
            EvidenceResponseDTO dto = convertActionEvidenceToDTO(evidence);
            evidenceList.add(dto);
        }
        
        // Get method execution evidences under this action
        List<VerificationMethodExecution> methodExecutions = 
            verificationMethodExecutionRepository
                .findByVerificationCheck_CaseCheckIdAndAction_Id(checkId, actionId);
        
        for (VerificationMethodExecution execution : methodExecutions) {
            List<VerificationMethodExecutionEvidence> methodEvidences = 
                verificationMethodExecutionEvidenceRepository
                    .findByMethodExecution_ExecutionId(execution.getExecutionId());
            
            for (VerificationMethodExecutionEvidence evidence : methodEvidences) {
                EvidenceResponseDTO dto = convertMethodEvidenceToDTO(evidence);
                evidenceList.add(dto);
            }
        }
        
        // Sort by uploaded date (newest first)
        evidenceList.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        
        return evidenceList;
    }

    // ==================== LEVEL 7: DOCUMENT LEVEL ====================
    /**
     * Get evidences by document ID
     */
    public List<EvidenceResponseDTO> getEvidenceByDocumentId(Long documentId) {
        List<EvidenceResponseDTO> evidenceList = new ArrayList<>();
        
        // Get action evidences by document
        List<VerificationActionEvidence> actionEvidences = 
            verificationActionEvidenceRepository.findByDocumentId(documentId);
        
        for (VerificationActionEvidence evidence : actionEvidences) {
            EvidenceResponseDTO dto = convertActionEvidenceToDTO(evidence);
            evidenceList.add(dto);
        }
        
        // Get method execution evidences by document
        List<VerificationMethodExecutionEvidence> methodEvidences = 
            verificationMethodExecutionEvidenceRepository.findByDocumentId(documentId);
        
        for (VerificationMethodExecutionEvidence evidence : methodEvidences) {
            EvidenceResponseDTO dto = convertMethodEvidenceToDTO(evidence);
            evidenceList.add(dto);
        }
        
        // Sort by uploaded date (newest first)
        evidenceList.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        
        return evidenceList;
    }

    // ==================== LEVEL 8: SOURCE LEVEL ====================
    /**
     * Get evidences by source (CANDIDATE_DOCUMENT / VENDOR_UPLOAD)
     */
    public List<EvidenceResponseDTO> getEvidenceBySource(String source) {
        List<EvidenceResponseDTO> evidenceList = new ArrayList<>();
        
        List<VerificationActionEvidence> actionEvidences = 
            verificationActionEvidenceRepository.findBySource(
                com.org.bgv.vendor.dto.EvidenceSource.valueOf(source));
        
        for (VerificationActionEvidence evidence : actionEvidences) {
            EvidenceResponseDTO dto = convertActionEvidenceToDTO(evidence);
            evidenceList.add(dto);
        }
        
        // Sort by uploaded date (newest first)
        evidenceList.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        
        return evidenceList;
    }

    // ==================== LEVEL 9: CHECK + SOURCE ====================
    /**
     * Get evidences by check and source
     */
    public List<EvidenceResponseDTO> getEvidenceByCheckAndSource(Long checkId, String source) {
        List<EvidenceResponseDTO> evidenceList = new ArrayList<>();
        
        List<VerificationActionEvidence> actionEvidences = 
            verificationActionEvidenceRepository
                .findByVerificationCaseCheck_CaseCheckIdAndSource(
                    checkId, 
                    com.org.bgv.vendor.dto.EvidenceSource.valueOf(source));
        
        for (VerificationActionEvidence evidence : actionEvidences) {
            EvidenceResponseDTO dto = convertActionEvidenceToDTO(evidence);
            evidenceList.add(dto);
        }
        
        // Sort by uploaded date (newest first)
        evidenceList.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        
        return evidenceList;
    }

    // ==================== LEVEL 10: ALL EVIDENCE FOR OBJECT ====================
    /**
     * Get all evidence (action + method) for a specific object
     * This combines both tables for a given object
     */
    public List<EvidenceResponseDTO> getAllEvidenceForObject(String objectType, Long objectId) {
        List<EvidenceResponseDTO> evidenceList = new ArrayList<>();
        
        // Get method execution evidences for this object
        List<VerificationMethodExecution> methodExecutions = 
            verificationMethodExecutionRepository
                .findByObjectTypeAndObjectId(objectType, objectId);
        
        for (VerificationMethodExecution execution : methodExecutions) {
            // Get method execution evidences
            List<VerificationMethodExecutionEvidence> methodEvidences = 
                verificationMethodExecutionEvidenceRepository
                    .findByMethodExecution_ExecutionId(execution.getExecutionId());
            
            for (VerificationMethodExecutionEvidence evidence : methodEvidences) {
                EvidenceResponseDTO dto = convertMethodEvidenceToDTO(evidence);
                dto.setObjectType(objectType);
                dto.setObjectId(objectId);
                evidenceList.add(dto);
            }
            
            // Get action evidences related to this execution's action
            if (execution.getAction() != null) {
                List<VerificationActionEvidence> actionEvidences = 
                    verificationActionEvidenceRepository
                        .findByAction_Id(execution.getAction().getId());
                
                for (VerificationActionEvidence evidence : actionEvidences) {
                    EvidenceResponseDTO dto = convertActionEvidenceToDTO(evidence);
                    evidenceList.add(dto);
                }
            }
        }
        
        // Sort by uploaded date (newest first)
        evidenceList.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        
        return evidenceList;
    }

    // ==================== PRIVATE CONVERTER METHODS ====================
    
    private EvidenceResponseDTO convertActionEvidenceToDTO(VerificationActionEvidence evidence) {
        EvidenceResponseDTO dto = new EvidenceResponseDTO();
        dto.setId(evidence.getId());
        dto.setEvidenceType("ACTION");
        if (evidence.getSource() != null) {
            dto.setSource(evidence.getSource().name());
        }
        dto.setFileName(evidence.getFileName());
        dto.setOriginalFileName(evidence.getOriginalFileName());
        dto.setFileUrl(evidence.getFileUrl());
        dto.setFileType(evidence.getFileType());
        dto.setFileSize(evidence.getFileSize());
      //  dto.setStorageKey(evidence.getStorageKey());
      //  dto.setDocumentId(evidence.getDocumentId());
        
        // Set uploaded by user name
        if (evidence.getUploadedBy() != null) {
            try {
                dto.setUploadedBy(
                    userServiceUtil.getUserDetails(evidence.getUploadedBy()).getFullName()
                );
            } catch (Exception e) {
                log.error("Error fetching user details for ID: {}", evidence.getUploadedBy(), e);
                dto.setUploadedBy("Unknown User");
            }
        }
        
        dto.setUploadedAt(evidence.getUploadedAt() != null ? 
            evidence.getUploadedAt().toString() : null);
       // dto.setArchived(evidence.isArchived());
        
        // Set status from action
        if (evidence.getAction() != null && evidence.getAction().getStatus() != null) {
            dto.setStatus(evidence.getAction().getStatus().name());
        }
        
        // Set check ID
        if (evidence.getVerificationCaseCheck() != null) {
          //  dto.setCheckId(evidence.getVerificationCaseCheck().getCaseCheckId());
        }
        
        return dto;
    }

    private EvidenceResponseDTO convertMethodEvidenceToDTO(VerificationMethodExecutionEvidence evidence) {
        EvidenceResponseDTO dto = new EvidenceResponseDTO();
        dto.setId(evidence.getEvidenceId());
        dto.setEvidenceType("METHOD_EXECUTION");
        dto.setFileName(evidence.getFileName());
        dto.setOriginalFileName(evidence.getOriginalFileName());
        dto.setFileUrl(evidence.getFileUrl());
        dto.setFileType(evidence.getFileType());
        dto.setFileSize(evidence.getFileSize());
      //  dto.setStorageKey(evidence.getStorageKey());
      //  dto.setDocumentId(evidence.getDocumentId());
        dto.setRemarks(evidence.getRemarks());
        dto.setNotes(evidence.getNotes());
      //  dto.setContentType(evidence.getContentType());
     //   dto.setKey(evidence.getKey());
        
        // Set uploaded by user name
        if (evidence.getUploadedBy() != null) {
            try {
                dto.setUploadedBy(
                    userServiceUtil.getUserDetails(evidence.getUploadedBy()).getFullName()
                );
            } catch (Exception e) {
                log.error("Error fetching user details for ID: {}", evidence.getUploadedBy(), e);
                dto.setUploadedBy("Unknown User");
            }
        }
        
        dto.setUploadedAt(evidence.getUploadedAt() != null ? 
            evidence.getUploadedAt().toString() : null);
       // dto.setArchived(evidence.getArchived() != null ? evidence.getArchived() : false);
        
        // Set status from method execution
        if (evidence.getMethodExecution() != null && 
            evidence.getMethodExecution().getStatus() != null) {
            dto.setStatus(evidence.getMethodExecution().getStatus().name());
        }
        
        // Set check ID
        if (evidence.getVerificationCaseCheck() != null) {
            dto.setCheckId(evidence.getVerificationCaseCheck().getCaseCheckId());
        }
        
        // Set execution ID
        if (evidence.getMethodExecution() != null) {
            dto.setExecutionId(evidence.getMethodExecution().getExecutionId());
            
            // Set object type and ID from method execution
            if (evidence.getMethodExecution().getObjectType() != null) {
                dto.setObjectType(evidence.getMethodExecution().getObjectType());
            }
            if (evidence.getMethodExecution().getObjectId() != null) {
                dto.setObjectId(evidence.getMethodExecution().getObjectId());
            }
        }
        
        return dto;
    }

    // ==================== BULK EVIDENCE RETRIEVAL ====================
    
    /**
     * Get evidences for multiple checks (for dashboard)
     */
    public List<EvidenceResponseDTO> getEvidenceForChecks(List<Long> checkIds) {
        List<EvidenceResponseDTO> allEvidence = new ArrayList<>();
        
        for (Long checkId : checkIds) {
            allEvidence.addAll(getEvidenceForCheck(checkId));
        }
        
        // Sort by uploaded date (newest first)
        allEvidence.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        
        return allEvidence;
    }

    /**
     * Get recent evidences (for dashboard quick view)
     */
    public List<EvidenceResponseDTO> getRecentEvidences(int limit) {
        List<EvidenceResponseDTO> allEvidence = new ArrayList<>();
        
        // Get all action evidences
        List<VerificationActionEvidence> actionEvidences = 
            verificationActionEvidenceRepository.findAll();
        
        for (VerificationActionEvidence evidence : actionEvidences) {
            if (!evidence.isArchived()) {
                EvidenceResponseDTO dto = convertActionEvidenceToDTO(evidence);
                allEvidence.add(dto);
            }
        }
        
        // Get all method execution evidences
        List<VerificationMethodExecutionEvidence> methodEvidences = 
            verificationMethodExecutionEvidenceRepository.findAll();
        
        for (VerificationMethodExecutionEvidence evidence : methodEvidences) {
            if (evidence.getArchived() == null || !evidence.getArchived()) {
                EvidenceResponseDTO dto = convertMethodEvidenceToDTO(evidence);
                allEvidence.add(dto);
            }
        }
        
        // Sort by uploaded date (newest first)
        allEvidence.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        
        // Limit results
        return allEvidence.stream().limit(limit).collect(Collectors.toList());
    }
}