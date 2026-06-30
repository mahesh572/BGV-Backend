package com.org.bgv.user.kyc.service;

import org.springframework.stereotype.Service;

import com.org.bgv.user.kyc.requests.PanVerificationRequest;
import com.org.bgv.user.kyc.requests.PanVerificationResponse;

@Service
public class PanKycService {

    private final PanProviderFactory providerFactory;

    public PanKycService(PanProviderFactory providerFactory) {
        this.providerFactory = providerFactory;
    }

    public PanVerificationResponse verify(PanVerificationRequest request) {

        // 1. basic validation
        if (!request.getPanNumber().matches("^[A-Z]{5}[0-9]{4}[A-Z]{1}$")) {
            throw new RuntimeException("Invalid PAN format");
        }

        // 2. call provider (NO direct dependency)
        PanVerificationProvider provider = providerFactory.getProvider();

        PanVerificationResponse response = provider.verify(request);

        // 3. you can add matching rules here later
        return response;
    }
}