package com.org.bgv.user.kyc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PanProviderFactory {

    @Value("${kyc.pan.provider}")
    private String activeProvider;

    private final Map<String, PanVerificationProvider> providers;

    public PanProviderFactory(List<PanVerificationProvider> providerList) {
        this.providers = new HashMap<>();
        for (PanVerificationProvider p : providerList) {
            providers.put(p.getProviderName(), p);
        }
    }

    public PanVerificationProvider getProvider() {
        return providers.get(activeProvider);
    }
}
