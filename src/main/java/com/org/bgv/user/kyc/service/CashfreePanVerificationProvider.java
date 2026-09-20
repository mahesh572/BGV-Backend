package com.org.bgv.user.kyc.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.org.bgv.common.client.IntegrationClient;
import com.org.bgv.user.enums.KycProvider;
import com.org.bgv.user.kyc.config.CashfreeProperties;
import com.org.bgv.user.kyc.requests.PanVerificationRequest;
import com.org.bgv.user.kyc.requests.PanVerificationResponse;
import com.org.bgv.user.kyc.response.CashfreePanResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CashfreePanVerificationProvider implements PanVerificationProvider {

    private final IntegrationClient client;
    
    private final PanVerificationResponseMapper panVerificationResponseMapper;
    private final CashfreeProperties properties;

    @Override
    public PanVerificationResponse verify(PanVerificationRequest request) {

        Map<String, String> headers = Map.of(
                "x-client-id", properties.getClientId(),
                "x-client-secret", properties.getClientSecret()
        );

        CashfreePanResponse response = client.post(
                properties.getBaseUrl(),
                "/verification/pan",
                headers,
                request,
                CashfreePanResponse.class);

        return panVerificationResponseMapper.map(response);
    }

	@Override
	public KycProvider getProviderName() {
		
		return KycProvider.CASHFREE;
	}
}