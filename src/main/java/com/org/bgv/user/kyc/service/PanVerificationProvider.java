package com.org.bgv.user.kyc.service;

import com.org.bgv.user.kyc.requests.PanVerificationRequest;
import com.org.bgv.user.kyc.requests.PanVerificationResponse;

public interface PanVerificationProvider {

    PanVerificationResponse verify(PanVerificationRequest request);

    String getProviderName();
}