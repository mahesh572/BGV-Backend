package com.org.bgv.policy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.policy.entity.PolicyVersion;
import com.org.bgv.policy.enums.ConsentStatus;
import com.org.bgv.policy.enums.EntityType;
import com.org.bgv.policy.enums.PolicyAudience;
import com.org.bgv.policy.enums.VersionStatus;

public interface PolicyVersionRepository extends JpaRepository<PolicyVersion, String> {

    List<PolicyVersion> findByPolicyIdOrderByPublishedAtDesc(String policyId);

    Optional<PolicyVersion> findByPolicyPolicyCodeAndCurrentVersionTrue(String policyCode);

    Optional<PolicyVersion> findByPolicyPolicyCodeAndVersion(String policyCode, String version);

    List<PolicyVersion> findByPolicyIdAndStatusOrderByPublishedAtDesc(
            String policyId,
            VersionStatus status);

    Optional<PolicyVersion> findByPolicyIdAndCurrentVersionTrue(String policyId);
    
    Optional<PolicyVersion> findByPolicyIdAndCurrentVersionTrueAndStatus(
            String policyId,
            VersionStatus status);
    /*
    boolean existsByEntityIdAndEntityTypeAndPolicyVersion(
            Long entityId,
            EntityType entityType,
            PolicyVersion policyVersion);
            */
    
    Optional<PolicyVersion> findTopByPolicyAudienceAndStatusOrderByPublishedAtDesc(
            PolicyAudience audience,
            VersionStatus status);
    
    
}