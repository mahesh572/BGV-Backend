package com.org.bgv.common;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class CommonUtils {

	private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$!%";
    private static final int PASSWORD_LENGTH = 10;

    public static String generateTempPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHAR_SET.length());
            sb.append(CHAR_SET.charAt(index));
        }
        return sb.toString();
    }
	
    public static String cleanBase64(String base64) {

        if (base64 == null || base64.isBlank()) {
            return null;
        }

        try {
            base64 = base64.trim();

            // 🔥 Remove wrapping quotes if present
            if (base64.startsWith("\"") && base64.endsWith("\"")) {
                base64 = base64.substring(1, base64.length() - 1);
            }

            // Remove data URI prefix
            if (base64.startsWith("data:")) {
                base64 = base64.substring(base64.indexOf(",") + 1);
            }

            // Remove whitespace/newlines
            base64 = base64.replaceAll("\\s+", "");

            // URL decode if needed
            if (base64.contains("%")) {
                base64 = java.net.URLDecoder.decode(base64, StandardCharsets.UTF_8);
            }

            // Fix padding
            int padding = base64.length() % 4;
            if (padding > 0) {
                base64 += "=".repeat(4 - padding);
            }

            // 🔥 Real validation (better than regex)
            java.util.Base64.getDecoder().decode(base64);

            return base64;

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Base64 data format", e);
        }
    }


}
