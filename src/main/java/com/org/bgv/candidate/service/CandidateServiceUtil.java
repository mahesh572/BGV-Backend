package com.org.bgv.candidate.service;

import org.springframework.stereotype.Service;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.service.util.CompanyServiceUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CandidateServiceUtil {

    private final CandidateRepository candidateRepository;

    public Candidate getCandidate(Long userId, Long companyId) {

        return candidateRepository
                .findByUserUserIdAndCompanyId(userId, companyId)
                .orElseThrow(() -> new RuntimeException(
                        "Candidate not found for userId: " + userId +
                        ", companyId: " + companyId
                ));
    }
}