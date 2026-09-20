package com.org.bgv.user.kyc.service;


import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.user.enums.KycProvider;

@Service
public class DigilockerProviderFactory {

    @Value("${kyc.digilocker.provider}")
    private KycProvider activeProvider;

    private final Map<KycProvider, DigilockerVerificationProvider> providers =
            new EnumMap<>(KycProvider.class);

    public DigilockerProviderFactory(
            List<DigilockerVerificationProvider> providerList) {

        providerList.forEach(provider ->
                providers.put(provider.getProviderName(), provider));
    }

    public DigilockerVerificationProvider getProvider() {

        DigilockerVerificationProvider provider =
                providers.get(activeProvider);

        if (provider == null) {
            throw new BusinessException(
                    "Digilocker provider '" + activeProvider + "' is not configured.");
        }

        return provider;
    }
}
