package com.org.bgv.policy.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.policy.entity.Policy;
import com.org.bgv.policy.enums.PolicyAudience;
import com.org.bgv.policy.enums.PolicyStatus;

public interface PolicyRepository extends JpaRepository<Policy, String> {

    Optional<Policy> findByPolicyCode(String policyCode);

    boolean existsByPolicyCode(String policyCode);
    
    List<Policy> findByAudienceInAndStatus(
            List<PolicyAudience> audiences,
            PolicyStatus status);
    
    Policy findByAudienceAndStatus(
            PolicyAudience audience,
            PolicyStatus status);
}
