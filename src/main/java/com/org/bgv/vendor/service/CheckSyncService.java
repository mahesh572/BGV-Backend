package com.org.bgv.vendor.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.bgv.candidate.entity.CandidateVerification;
import com.org.bgv.candidate.repository.CandidateVerificationRepository;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.VerificationStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CheckSyncService {
	private final CandidateVerificationRepository candidateVerificationRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void markSectionActionRequired(Long candidateId,Long caseId, String sectionKey) throws JsonMappingException, JsonProcessingException {

       CandidateVerification verification =
                candidateVerificationRepository
                        .findByCandidateIdAndVerificationCaseCaseId(candidateId,caseId)
                        .orElseGet(null);

        Map<String, Map<String, Object>> sectionStatusMap =
                objectMapper.readValue(
                        verification.getSectionStatus(),
                        new TypeReference<>() {}
                );

        Map<String, Object> section =
                sectionStatusMap.getOrDefault(sectionKey, new HashMap<>());

        section.put("status", CaseCheckStatus.ACTION_REQUIRED);
        section.put("lastUpdated", LocalDateTime.now().toString());

        sectionStatusMap.put(sectionKey, section);

        verification.setSectionStatus(
                objectMapper.writeValueAsString(sectionStatusMap)
        );

        verification.setStatus(VerificationStatus.IN_PROGRESS);

        candidateVerificationRepository.save(verification);
    }
}
