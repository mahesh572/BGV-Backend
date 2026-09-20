package com.org.bgv.user.kyc.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CashfreeProperties.class)
public class CashfreeConfiguration {
}