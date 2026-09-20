package com.org.bgv.user.kyc.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.org.bgv.common.client.IntegrationClient;
import com.org.bgv.user.enums.KycProvider;
import com.org.bgv.user.kyc.config.CashfreeProperties;
import com.org.bgv.user.kyc.requests.CreateDigilockerSessionRequest;
import com.org.bgv.user.kyc.requests.VerifyDigilockerAccountRequest;
import com.org.bgv.user.kyc.response.CreateDigilockerSessionResponse;
import com.org.bgv.user.kyc.response.DigilockerDocumentResponse;
import com.org.bgv.user.kyc.response.DigilockerStatusResponse;
import com.org.bgv.user.kyc.response.VerifyDigilockerAccountResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CashfreeDigilockerProvider
        implements DigilockerVerificationProvider {

    private final IntegrationClient integrationClient;
    private final CashfreeProperties properties;
    private final DigilockerResponseMapper mapper;

    @Override
    public VerifyDigilockerAccountResponse verifyAccount(
            VerifyDigilockerAccountRequest request) {

        log.info("Calling Cashfree Verify DigiLocker Account API");

        log.info("Base URL: {}", properties.getBaseUrl());
        log.info("Path: /verification/digilocker/verify-account");
        log.info("Headers: {}", maskedHeaders());
        log.info("Request: {}", request);

        VerifyDigilockerAccountResponse response =
                integrationClient.post(
                        properties.getBaseUrl(),
                        "/verification/digilocker/verify-account",
                        headers(),
                        request,
                        VerifyDigilockerAccountResponse.class);

        log.info("Cashfree Verify Account Response: {}", response);

        return mapper.toVerifyAccountResponse(response);
    }

    @Override
    public CreateDigilockerSessionResponse createSession(
            CreateDigilockerSessionRequest request) {

        log.info("Calling Cashfree Create DigiLocker Session API");

        log.info("Base URL: {}", properties.getBaseUrl());
        log.info("Path: /verification/digilocker");
        log.info("Headers: {}", maskedHeaders());
        log.info("Request: {}", request);

        CreateDigilockerSessionResponse response =
                integrationClient.post(
                        properties.getBaseUrl(),
                        "/verification/digilocker",
                        headers(),
                        request,
                        CreateDigilockerSessionResponse.class);

        log.info("Cashfree Create Session Response: {}", response);

        return mapper.toCreateSessionResponse(response);
    }
    @Override
    public DigilockerStatusResponse getStatus(
            String verificationId,
            Long referenceId) {

        String path =
                "/verification/digilocker?reference_id="
                        + referenceId
                        + "&verification_id="
                        + verificationId;

        log.info("Calling Cashfree DigiLocker Status API");
        log.info("Path: {}", path);
        log.info("Headers: {}", maskedHeaders());

        DigilockerStatusResponse response =
                integrationClient.get(
                        properties.getBaseUrl(),
                        path,
                        headers(),
                        DigilockerStatusResponse.class);

        log.info("Cashfree Status Response: {}", response);

        return mapper.toStatusResponse(response);
    }

    @Override
    public DigilockerDocumentResponse getDocument(
            String verificationId,
            Long referenceId) {

        String path =
                "/verification/digilocker/document"
                        + "?reference_id=" + referenceId
                        + "&verification_id=" + verificationId;

        log.info("Calling Cashfree DigiLocker Document API");
        log.info("Path: {}", path);
        log.info("Headers: {}", maskedHeaders());

        DigilockerDocumentResponse response =
                integrationClient.get(
                        properties.getBaseUrl(),
                        path,
                        headers(),
                        DigilockerDocumentResponse.class);

        log.info("Cashfree Document Response: {}", response);

        return mapper.toDocumentResponse(response);
    }

    @Override
    public KycProvider getProviderName() {
        return KycProvider.CASHFREE;
    }

    private Map<String, String> headers() {

        return Map.of(
                "x-client-id", properties.getClientId(),
                "x-client-secret", properties.getClientSecret()
        );
    }
    
    
    private Map<String, String> maskedHeaders() {

        return Map.of(
                "x-client-id", properties.getClientId(),
                "x-client-secret", "********"
        );
    }
}