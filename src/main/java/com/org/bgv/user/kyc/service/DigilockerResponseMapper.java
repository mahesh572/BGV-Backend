package com.org.bgv.user.kyc.service;

import org.springframework.stereotype.Component;

import com.org.bgv.user.kyc.response.CreateDigilockerSessionResponse;
import com.org.bgv.user.kyc.response.DigilockerDocumentResponse;
import com.org.bgv.user.kyc.response.DigilockerStatusResponse;
import com.org.bgv.user.kyc.response.VerifyDigilockerAccountResponse;

@Component
public class DigilockerResponseMapper {

    public VerifyDigilockerAccountResponse toVerifyAccountResponse(
            VerifyDigilockerAccountResponse response) {

        if (response == null) {
            return null;
        }

        return VerifyDigilockerAccountResponse.builder()
                .verificationId(response.getVerificationId())
                .referenceId(response.getReferenceId())
                .mobileNumber(response.getMobileNumber())
                .aadhaarNumber(response.getAadhaarNumber())
                .status(response.getStatus())
                .digilockerId(response.getDigilockerId())
                .build();
    }

    public CreateDigilockerSessionResponse toCreateSessionResponse(
            CreateDigilockerSessionResponse response) {

        if (response == null) {
            return null;
        }

        return CreateDigilockerSessionResponse.builder()
                .verificationId(response.getVerificationId())
                .referenceId(response.getReferenceId())
                .url(response.getUrl())
                .status(response.getStatus())
                .documentRequested(response.getDocumentRequested())
                .redirectUrl(response.getRedirectUrl())
                .userFlow(response.getUserFlow())
                .build();
    }

    public DigilockerStatusResponse toStatusResponse(
            DigilockerStatusResponse response) {

        if (response == null) {
            return null;
        }

        return DigilockerStatusResponse.builder()
                .verificationId(response.getVerificationId())
                .referenceId(response.getReferenceId())
                .status(response.getStatus())
              //  .digilockerId(response.getDigilockerId())
                .build();
    }

    public DigilockerDocumentResponse toDocumentResponse(
    		DigilockerDocumentResponse response) {

        if (response == null) {
            return null;
        }

        return DigilockerDocumentResponse.builder()
                .documentType(response.getDocumentType())
                .providerReference(response.getProviderReference())
                .document(response.getDocument())
                .rawResponse(response.getRawResponse())
                .build();
    }
}