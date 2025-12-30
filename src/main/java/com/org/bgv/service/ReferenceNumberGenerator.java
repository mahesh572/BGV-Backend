package com.org.bgv.service;

import java.time.Year;

import org.springframework.stereotype.Component;

import com.org.bgv.entity.ReferenceSequence;
import com.org.bgv.repository.ReferenceSequenceRepository;
import com.org.bgv.repository.SequenceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Centralized generator for:
 *  - Candidate ID   → CAND-2025-000123
 *  - Case Number    → CASE-2025-000025
 *  - Check Number   → CHK-2025-000025-003
 */

@Component
@RequiredArgsConstructor
public class ReferenceNumberGenerator {

	private final ReferenceSequenceRepository repository;

    @Transactional
    public String generateCandidateRef() {
        return generate("CANDIDATE", "CAND");
    }

    @Transactional
    public String generateCaseNumber() {
        return generate("CASE", "CASE");
    }
    
    @Transactional
    public String generateCheckCaseNumber() {
        return generate("CHECK", "CHECK");
    }

    private String generate(String type, String prefix) {
        int year = Year.now().getValue();

        ReferenceSequence seq = repository
            .lockByTypeAndYear(type, year)
            .orElseGet(() ->
                repository.save(
                    ReferenceSequence.builder()
                        .seqType(type)
                        .year(year)
                        .currentValue(0)
                        .build()
                )
            );

        seq.setCurrentValue(seq.getCurrentValue() + 1);
        repository.save(seq);

        return String.format(
            "%s-%d-%06d",
            prefix,
            year,
            seq.getCurrentValue()
        );
    }
}
