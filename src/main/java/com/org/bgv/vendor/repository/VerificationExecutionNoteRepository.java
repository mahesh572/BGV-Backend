package com.org.bgv.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.vendor.entity.VerificationExecutionNote;

public interface VerificationExecutionNoteRepository
        extends JpaRepository<VerificationExecutionNote, Long> {

    List<VerificationExecutionNote> findByExecutionExecutionIdOrderByCreatedAtDesc(
            Long executionId);

    List<VerificationExecutionNote> findByCheckIdOrderByCreatedAtDesc(
            Long checkId);

    List<VerificationExecutionNote> findByCaseIdOrderByCreatedAtDesc(
            Long caseId);

    List<VerificationExecutionNote> findByObjectIdOrderByCreatedAtDesc(
            Long objectId);

}