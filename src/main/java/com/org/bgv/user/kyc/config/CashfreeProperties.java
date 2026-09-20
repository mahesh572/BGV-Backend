package com.org.bgv.user.kyc.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "cashfree")
public class CashfreeProperties {

    private String baseUrl;

    private String clientId;

    private String clientSecret;
}