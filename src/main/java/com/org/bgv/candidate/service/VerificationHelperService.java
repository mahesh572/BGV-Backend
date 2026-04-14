package com.org.bgv.candidate.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.bgv.candidate.dto.VerificationSectionDTO;
import com.org.bgv.candidate.entity.CandidateVerification;
import com.org.bgv.candidate.repository.CandidateVerificationRepository;
import com.org.bgv.constants.SectionStatus;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.repository.VerificationCaseDocumentRepository;
import com.org.bgv.repository.VerificationCaseRepository;
import com.org.bgv.service.DocumentService;
import com.org.bgv.service.EducationService;
import com.org.bgv.service.IdentityProofService;
import com.org.bgv.service.ProfileAddressService;
import com.org.bgv.service.ProfileService;
import com.org.bgv.service.WorkExperienceService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationHelperService {
	
	    private final CandidateVerificationRepository candidateVerificationRepository;
	    private final ObjectMapper objectMapper;
	    
	
	  public VerificationSectionDTO getSectionStatusByCaseAndCandidate(
	            Long candidateId,
	            Long caseId,
	            String sectionId) {

	        log.info("Fetching section status for candidate: {}, case: {}, section: {}",
	                candidateId, caseId, sectionId);

	        // 1️⃣ Fetch Candidate Verification
	        CandidateVerification verification =
	                candidateVerificationRepository
	                        .findByCandidateIdAndVerificationCaseCaseId(candidateId, caseId)
	                        .orElseThrow(() ->
	                                new EntityNotFoundException("Verification not found"));

	        // 2️⃣ Get maps
	        Map<String, Map<String, Object>> statusMap = getSectionStatusMap(verification);
	        

	        // 3️⃣ Prepare response DTO
	        VerificationSectionDTO sectionDTO = new VerificationSectionDTO();
	        sectionDTO.setSectionId(sectionId);

	        // 4️⃣ Label (optional mapping)
	      //  sectionDTO.setLabel(getSectionLabel(sectionId));

	        // 6️⃣ Status + Last Updated
	        Map<String, Object> statusData = statusMap.get(sectionId);
	        if (statusData != null) {
	            String statusStr = (String) statusData.getOrDefault("status", "NOT_STARTED");
	            sectionDTO.setStatus(SectionStatus.fromString(statusStr));

	            Object lastUpdated = statusData.get("lastUpdated");
	            if (lastUpdated != null) {
	                sectionDTO.setLastUpdated(LocalDateTime.parse(lastUpdated.toString()));
	            }
	        } else {
	            sectionDTO.setStatus(SectionStatus.NOT_STARTED);
	        }

	        return sectionDTO;
	    }
	    
	    
	  @SuppressWarnings("unchecked")
	    public Map<String, Map<String, Object>> getSectionStatusMap(CandidateVerification verification) {
	        try {
	        	log.info("getSectionStatusMap::::::verification.getSectionStatus():::::::::::::{}",verification.getSectionStatus());
	            if (verification.getSectionStatus() != null) {
	                return objectMapper.readValue(verification.getSectionStatus(), 
	                    new TypeReference<Map<String, Map<String, Object>>>() {});
	            }
	        } catch (Exception e) {
	            log.error("Error parsing section status: {}", e.getMessage());
	        }
	        return new HashMap<>();
	    }

}
