package com.org.bgv.data.seed;


import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.org.bgv.policy.entity.Policy;
import com.org.bgv.policy.entity.PolicyVersion;
import com.org.bgv.policy.enums.PolicyAudience;
import com.org.bgv.policy.enums.PolicyStatus;
import com.org.bgv.policy.enums.VersionStatus;
import com.org.bgv.policy.repository.PolicyRepository;
import com.org.bgv.policy.repository.PolicyVersionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PolicySeeder implements CommandLineRunner {

    private final PolicyRepository policyRepository;
    private final PolicyVersionRepository policyVersionRepository;

    @Override
    public void run(String... args) {

        seedPolicy(
                "PRIVACY_POLICY",
                "Privacy Policy",
                "Platform privacy policy.",
                PolicyAudience.ALL,
                "<h2>Privacy Policy</h2><p>Default privacy policy content.</p>");

        seedPolicy(
                "TERMS_AND_CONDITIONS",
                "Terms & Conditions",
                "Platform terms and conditions.",
                PolicyAudience.ALL,
                "<h2>Terms & Conditions</h2><p>Default terms and conditions.</p>");

        seedPolicy(
                "CANDIDATE_CONSENT",
                "Candidate Consent",
                "Consent for background verification.",
                PolicyAudience.CANDIDATE,
                "<h2>Candidate Consent</h2><p>Default candidate consent.</p>");

        seedPolicy(
                "EMPLOYER_AGREEMENT",
                "Employer Agreement",
                "Employer agreement.",
                PolicyAudience.EMPLOYER,
                "<h2>Employer Agreement</h2><p>Default employer agreement.</p>");

        seedPolicy(
                "VENDOR_AGREEMENT",
                "Vendor Agreement",
                "Vendor agreement.",
                PolicyAudience.VENDOR,
                "<h2>Vendor Agreement</h2><p>Default vendor agreement.</p>");
        
        seedPolicy(
                "STUDENT_PRIVACY_POLICY",
                "Student Privacy Policy",
                "Privacy policy applicable to students using the platform.",
                PolicyAudience.USER,
                "<h2>Student Privacy Policy</h2><p>Default student privacy policy.</p>");
    }

    
    private void seedPolicy(
            String policyCode,
            String name,
            String description,
            PolicyAudience audience,
            String content) {

        Policy policy = policyRepository.findByPolicyCode(policyCode)
                .orElseGet(() -> Policy.builder().build());

        // Update latest values
        policy.setPolicyCode(policyCode);
        policy.setName(name);
        policy.setDescription(description);
        policy.setAudience(audience);
        policy.setStatus(PolicyStatus.ACTIVE);

        policy = policyRepository.save(policy);

        PolicyVersion version = policyVersionRepository
                .findByPolicyPolicyCodeAndVersion(policyCode, "1.0")
                .orElseGet(() -> PolicyVersion.builder().build());

        version.setPolicy(policy);
        version.setVersion("1.0");
        version.setContent(content);
        version.setContentType("HTML");
        version.setChangeSummary("Initial Version");
        version.setEffectiveFrom(LocalDateTime.now());
        version.setCurrentVersion(true);
        version.setStatus(VersionStatus.PUBLISHED);

        if (version.getPublishedAt() == null) {
            version.setPublishedAt(LocalDateTime.now());
        }

        policyVersionRepository.save(version);
    }
    

}
