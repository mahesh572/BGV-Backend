package com.org.bgv.common;

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
        if (base64 == null) {
            return null;
        }

        // Remove data URI prefix if present
        if (base64.contains(",")) {
            base64 = base64.substring(base64.indexOf(",") + 1);
        }

        // Remove whitespace & line breaks
        return base64
                .replaceAll("\\s+", "")
                .trim();
    }
}
