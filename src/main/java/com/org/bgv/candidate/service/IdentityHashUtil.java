package com.org.bgv.candidate.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

@Component
public class IdentityHashUtil {

    private static final String SALT = "BGV_SECURE_SALT_2025"; // move to env

    public String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest((value + SALT).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encoded);
        } catch (Exception e) {
            throw new IllegalStateException("Hashing failed", e);
        }
    }

    public String last4(String value) {
        return value.substring(value.length() - 4);
    }
}

