package com.org.bgv.vendor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.vendor.entity.VerificationActionEvidence;

@Repository
public interface VerificationActionEvidenceRepository
        extends JpaRepository<VerificationActionEvidence, Long> {

    List<VerificationActionEvidence> findByAction_Id(Long actionId);

    Optional<VerificationActionEvidence> findById(Long id);

    boolean existsByAction_IdAndId(Long actionId, Long id);
    
    @Modifying
    @Query("DELETE FROM VerificationActionEvidence e WHERE e.action.verificationCase.caseId = :caseId")
    void deleteByActionVerificationCaseCaseId(@Param("caseId") Long caseId);
}