package com.org.bgv.policy.service;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.org.bgv.candidate.service.IdentityHashUtil;
import com.org.bgv.common.CommonUtils;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.policy.entity.Policy;
import com.org.bgv.policy.entity.PolicyConsent;
import com.org.bgv.policy.entity.PolicyVersion;
import com.org.bgv.policy.enums.ConsentStatus;
import com.org.bgv.policy.enums.ConsentType;
import com.org.bgv.policy.enums.EntityType;
import com.org.bgv.policy.enums.PolicyAudience;
import com.org.bgv.policy.enums.PolicyStatus;
import com.org.bgv.policy.enums.VersionStatus;
import com.org.bgv.policy.repository.PolicyConsentRepository;
import com.org.bgv.policy.repository.PolicyRepository;
import com.org.bgv.policy.repository.PolicyVersionRepository;
import com.org.bgv.policy.request.PolicyConsentRequest;
import com.org.bgv.policy.request.PolicyConsentResponse;
import com.org.bgv.policy.request.PolicyDetailsResponse;
import com.org.bgv.policy.service.PolicyService;
import com.org.bgv.s3.S3StorageService;
import com.org.bgv.service.ReferenceNumberGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyServiceImpl implements PolicyService {

    private final PolicyVersionRepository policyVersionRepository;
    private final PolicyConsentRepository policyConsentRepository;
    private final S3StorageService s3StorageService;
    private final IdentityHashUtil identityHashUtil;
    private final ReferenceNumberGenerator referenceNumberGenerator;
    private final PolicyConsentPdfService policyConsentPdfService;
    private final PolicyRepository policyRepository;

    @Override
    @Transactional
    public PolicyConsentResponse acceptPolicy(PolicyConsentRequest request) {

        log.info("Accepting policy. EntityId={}, EntityType={}, PolicyVersion={}",
                request.getEntityId(),
                request.getEntityType(),
                request.getPolicyVersionId());

        PolicyVersion policyVersion = policyVersionRepository
                .findById(request.getPolicyVersionId())
                .orElseThrow(() ->
                        new BusinessException("Policy version not found."));

        boolean alreadyAccepted =
                policyConsentRepository.existsByEntityIdAndEntityTypeAndPolicyVersionId(
                        request.getEntityId(),
                        request.getEntityType(),
                        request.getPolicyVersionId());

        if (alreadyAccepted) {
            throw new BusinessException("Policy has already been accepted.");
        }

        PolicyConsent consent = PolicyConsent.builder()
                .entityId(request.getEntityId())
                .entityType(request.getEntityType())
                .policyVersion(policyVersion)
                .consentType(ConsentType.POLICY)
                .consentGiven(Boolean.TRUE)
                .status(ConsentStatus.ACTIVE)
                .acceptedAt(LocalDateTime.now())
                .referenceNumber(referenceNumberGenerator.generatePolicyNumber())
                .ipAddress(request.getIpAddress())
                .userAgent(request.getUserAgent())
                .policyChecksum(
                        identityHashUtil.hash(policyVersion.getContent()))
                .build();

        /* ===============================
           Upload Signature
           =============================== */
        if (StringUtils.isNotBlank(request.getSignatureData())) {

            String cleanBase64 =
                    CommonUtils.cleanBase64(request.getSignatureData());

            MultipartFile signatureFile =
                    CommonUtils.base64ToMultipartFile(
                            cleanBase64,
                            "image/png",
                            "signature.png");

            Pair<String, String> upload =
                    s3StorageService.uploadFile(
                            signatureFile,
                            "policy/signatures");

            consent.setSignatureUrl(upload.getFirst());
            consent.setSignatureS3Key(upload.getSecond());
            consent.setSignatureHash(
                    identityHashUtil.hash(cleanBase64));
        }

        /* ===============================
           Upload Live Photo
           =============================== */
        if (request.getLivePhoto() != null &&
                !request.getLivePhoto().isEmpty()) {

            Pair<String, String> upload =
                    s3StorageService.uploadFile(
                            request.getLivePhoto(),
                            "policy/live-photo");

            consent.setLivePhotoUrl(upload.getFirst());
            consent.setLivePhotoS3Key(upload.getSecond());

            try {
                consent.setLivePhotoHash(
                        identityHashUtil.hash(
                                request.getLivePhoto().getBytes()));
            } catch (IOException e) {
                throw new RuntimeException("Unable to calculate photo hash.", e);
            }
        }

        /* ===============================
           Save Consent
           =============================== */
        PolicyConsent savedConsent =
                policyConsentRepository.save(consent);

        /* ===============================
           Generate Consent PDF
           =============================== */
        Pair<String, String> pdfUrl =
                policyConsentPdfService.generateConsentPdf(savedConsent);

        savedConsent.setConsentPdfUrl(pdfUrl.getFirst());
        savedConsent.setConsentPdfS3Key(pdfUrl.getSecond());

        savedConsent =
                policyConsentRepository.save(savedConsent);

        log.info("Policy accepted successfully. Reference={}",
                savedConsent.getReferenceNumber());
        
        String url = s3StorageService.generatePresignedUrlByKey(savedConsent.getConsentPdfS3Key(), 10, true);

        return PolicyConsentResponse.builder()
               // .consentId(savedConsent.getId())
               // .referenceNumber(savedConsent.getReferenceNumber())
              //  .policyVersion(savedConsent.getPolicyVersion().getVersion())
              //  .entityId(savedConsent.getEntityId())
              //  .entityType(savedConsent.getEntityType())
              //  .acceptedAt(savedConsent.getAcceptedAt())
              //  .signatureUrl(savedConsent.getSignatureUrl())
             //   .livePhotoUrl(savedConsent.getLivePhotoUrl())
             //   .consentPdfUrl(savedConsent.getConsentPdfUrl())
             //   .status(savedConsent.getStatus())
        		.preSignedUrl(url)
                .build();
    }
    
    
    @Transactional(readOnly = true)
    public PolicyDetailsResponse getPolicy(PolicyAudience audience) {
    	

    	Long userId = SecurityUtils.getCurrentUserId();
    	
        Policy policy = policyRepository
                .findByAudienceAndStatus(audience, PolicyStatus.ACTIVE);
                

        PolicyVersion version = policyVersionRepository
                .findByPolicyIdAndCurrentVersionTrueAndStatus(
                        policy.getId(),
                        VersionStatus.PUBLISHED)
                .orElseThrow(() ->
                        new BusinessException("Published version not found"));
        
        boolean hasAcceptedPolicy =
                policyConsentRepository.existsByEntityIdAndEntityTypeAndPolicyVersionIdAndStatus(
                        userId,
                        EntityType.USER,
                        version.getId(),
                        ConsentStatus.ACTIVE
                );

        return PolicyDetailsResponse.builder()
                .policyId(policy.getId())
                .policyVersionId(version.getId())
                .policyCode(policy.getPolicyCode())
                .policyName(policy.getName())
                .description(policy.getDescription())
                .audience(policy.getAudience())
                .version(version.getVersion())
                .content(version.getContent())
                .contentType(version.getContentType())
                .effectiveFrom(version.getEffectiveFrom())
                .documentUrl(version.getDocumentUrl())
                .hasAcceptedPolicy(hasAcceptedPolicy)
                .build();
    }

    /*
    @Transactional(readOnly = true)
    @Override
    public PolicyDetailsResponse getPolicy(PolicyAudience audience, Long userId) {

        Policy policy = policyRepository
                .findByAudienceAndStatus(audience, PolicyStatus.ACTIVE);

        PolicyVersion version = policyVersionRepository
                .findByPolicyIdAndCurrentVersionTrueAndStatus(
                        policy.getId(),
                        VersionStatus.PUBLISHED)
                .orElseThrow(() ->
                        new BusinessException("Published version not found"));

        boolean hasAcceptedPolicy =
                policyConsentRepository.existsByEntityIdAndEntityTypeAndPolicyVersionIdAndStatus(
                        userId,
                        EntityType.USER,
                        version.getId(),
                        ConsentStatus.ACTIVE
                );

        return PolicyDetailsResponse.builder()
                .policyId(policy.getId())
                .policyVersionId(version.getId())
                .policyCode(policy.getPolicyCode())
                .policyName(policy.getName())
                .description(policy.getDescription())
                .audience(policy.getAudience())
                .version(version.getVersion())
                .content(version.getContent())
                .contentType(version.getContentType())
                .effectiveFrom(version.getEffectiveFrom())
                .documentUrl(version.getDocumentUrl())
                .hasAcceptedPolicy(hasAcceptedPolicy)
                .build();
    }

	*/
}

