package com.org.bgv.user.kyc.service;

import org.springframework.stereotype.Component;

import com.org.bgv.user.kyc.requests.PanVerificationResponse;
import com.org.bgv.user.kyc.response.CashfreePanResponse;

@Component
public class PanVerificationResponseMapper
        implements ResponseMapper<CashfreePanResponse, PanVerificationResponse> {

    @Override
    public PanVerificationResponse map(CashfreePanResponse response) {

    	return PanVerificationResponse.builder()
                .verified(response.isValid())
                .pan(response.getPan())
                .providedName(response.getNameProvided())
                .registeredName(response.getRegisteredName())
                .fatherName(response.getFatherName())
                .panType(response.getType())
                .providerReference(String.valueOf(response.getReferenceId()))
                .message(response.getMessage())
                .rawResponse(response)
                .build();
    }
}
