package com.org.bgv.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.vendor.entity.VendorNote;

@Repository
public interface VendorNoteRepository extends JpaRepository<VendorNote, Long> {

    // =========================
    // ALL NOTES FOR A CHECK
    // =========================
    List<VendorNote>
    findByVerificationCaseCheck_CaseCheckIdOrderByCreatedAtDesc(
            Long checkId
    );

    // =========================
    // ONLY EMPLOYER VISIBLE
    // =========================
    List<VendorNote>
    findByVerificationCaseCheck_CaseCheckIdAndVisibleToEmployerTrueOrderByCreatedAtDesc(
            Long checkId
    );

    // =========================
    // ONLY CANDIDATE VISIBLE
    // =========================
    List<VendorNote>
    findByVerificationCaseCheck_CaseCheckIdAndVisibleToCandidateTrueOrderByCreatedAtDesc(
            Long checkId
    );

    // =========================
    // INTERNAL NOTES ONLY
    // =========================
    List<VendorNote>
    findByVerificationCaseCheck_CaseCheckIdAndIsInternalTrueOrderByCreatedAtDesc(
            Long checkId
    );

}