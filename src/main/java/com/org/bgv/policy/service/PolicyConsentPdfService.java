package com.org.bgv.policy.service;

import org.springframework.data.util.Pair;

import com.org.bgv.policy.entity.PolicyConsent;

public interface PolicyConsentPdfService {

    /**
     * Generates the signed policy consent PDF,
     * uploads it to storage (S3/File System),
     * and returns the public URL.
     *
     * @param policyConsent Policy consent details
     * @return Generated PDF URL
     */
	Pair<String, String> generateConsentPdf(PolicyConsent policyConsent);

}



