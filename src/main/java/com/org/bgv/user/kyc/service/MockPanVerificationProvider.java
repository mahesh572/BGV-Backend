package com.org.bgv.user.kyc.service;

import org.springframework.stereotype.Service;

import com.org.bgv.user.kyc.requests.PanVerificationRequest;
import com.org.bgv.user.kyc.requests.PanVerificationResponse;

@Service
public class MockPanVerificationProvider implements PanVerificationProvider {

    @Override
    public PanVerificationResponse verify(PanVerificationRequest request) {

        PanVerificationResponse response = new PanVerificationResponse();

        // fake validation logic
        boolean isValid = request.getPanNumber() != null &&
                request.getPanNumber().startsWith("ABCDE");

        response.setValid(isValid);
        response.setName(isValid ? request.getFullName().toUpperCase() : null);
        response.setDob(isValid ? request.getDob() : null);
        response.setStatus(isValid ? "ACTIVE" : "INVALID");
        response.setProviderRefId("MOCK-" + System.currentTimeMillis());

        return response;
    }

    @Override
    public String getProviderName() {
        return "MOCK_PROVIDER";
    }
}
