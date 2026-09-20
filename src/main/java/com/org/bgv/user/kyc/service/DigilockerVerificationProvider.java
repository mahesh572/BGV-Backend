package com.org.bgv.user.kyc.service;

import com.org.bgv.user.enums.KycProvider;
import com.org.bgv.user.kyc.requests.CreateDigilockerSessionRequest;
import com.org.bgv.user.kyc.requests.VerifyDigilockerAccountRequest;
import com.org.bgv.user.kyc.response.CreateDigilockerSessionResponse;
import com.org.bgv.user.kyc.response.DigilockerDocumentResponse;
import com.org.bgv.user.kyc.response.DigilockerStatusResponse;
import com.org.bgv.user.kyc.response.VerifyDigilockerAccountResponse;

public interface DigilockerVerificationProvider {

    VerifyDigilockerAccountResponse verifyAccount(
            VerifyDigilockerAccountRequest request);

    CreateDigilockerSessionResponse createSession(
            CreateDigilockerSessionRequest request);

    DigilockerStatusResponse getStatus(
            String verificationId,
            Long referenceId);

    DigilockerDocumentResponse getDocument(
            String verificationId,
            Long referenceId);
            

    KycProvider getProviderName();
}
