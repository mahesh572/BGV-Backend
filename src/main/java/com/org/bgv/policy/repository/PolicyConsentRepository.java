package com.org.bgv.policy.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.policy.entity.PolicyConsent;
import com.org.bgv.policy.enums.ConsentStatus;
import com.org.bgv.policy.enums.EntityType;

public interface PolicyConsentRepository extends JpaRepository<PolicyConsent, String> {

    List<PolicyConsent> findByEntityIdAndEntityType(
            Long entityId,
            EntityType entityType);

    List<PolicyConsent> findByEntityIdAndEntityTypeAndStatus(
            Long entityId,
            EntityType entityType,
            ConsentStatus status);

    Optional<PolicyConsent> findByReferenceNumber(String referenceNumber);

    List<PolicyConsent> findByPolicyVersionId(String policyVersionId);

    boolean existsByEntityIdAndEntityTypeAndPolicyVersionId(
            Long entityId,
            EntityType entityType,
            String policyVersionId);
    
    boolean existsByEntityIdAndEntityTypeAndPolicyVersionIdAndStatus(
            Long entityId,
            EntityType entityType,
            String policyVersionId,
            ConsentStatus status
    );
}