package com.org.bgv.policy.service;

import java.util.List;

import com.org.bgv.policy.entity.Policy;
import com.org.bgv.policy.entity.PolicyVersion;
import com.org.bgv.policy.enums.PolicyAudience;
import com.org.bgv.policy.request.PolicyConsentRequest;
import com.org.bgv.policy.request.PolicyConsentResponse;
import com.org.bgv.policy.request.PolicyDetailsResponse;

public interface PolicyService {

  //  Policy createPolicy(CreatePolicyRequest request);

  //  PolicyVersion createVersion(String policyCode, CreatePolicyVersionRequest request);

  //  PolicyVersion publishVersion(String versionId);

 //   PolicyVersion getCurrentVersion(String policyCode);

 //   List<PolicyVersion> getVersions(String policyCode);
	
	PolicyDetailsResponse getPolicy(PolicyAudience audience);
	
	// PolicyDetailsResponse getPolicy(PolicyAudience audience, Long userId);

    PolicyConsentResponse acceptPolicy(PolicyConsentRequest request);

  //  List<PolicyAcceptance> getAcceptedPolicies(String entityId);
}
