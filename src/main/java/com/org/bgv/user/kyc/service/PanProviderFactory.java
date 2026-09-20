package com.org.bgv.user.kyc.service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.org.bgv.user.enums.KycProvider;

@Service
public class PanProviderFactory {

    private final KycProvider activeProvider;

    private final Map<KycProvider, PanVerificationProvider> providers =
            new EnumMap<>(KycProvider.class);

    public PanProviderFactory(
            @Value("${kyc.pan.provider}") KycProvider activeProvider,
            List<PanVerificationProvider> providerList) {

        this.activeProvider = activeProvider;

        providerList.forEach(provider ->
                providers.put(provider.getProviderName(), provider));
    }

    public PanVerificationProvider getProvider() {

        PanVerificationProvider provider = providers.get(activeProvider);

        if (provider == null) {
            throw new IllegalStateException(
                    "PAN Provider not configured: " + activeProvider);
        }

        return provider;
    }
}