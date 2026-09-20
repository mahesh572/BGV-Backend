package com.org.bgv.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;


@Slf4j
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
    
    
    public static MultipartFile base64ToMultipartFile(String base64Data, String mimeType, String fileName) {
        try {
            // Remove data URL prefix if present
            if (base64Data.contains(",")) {
                base64Data = base64Data.split(",")[1];
            }
            
            log.info("base64Data::::::::::::::::::::{}",base64Data);

            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

            // Return anonymous implementation of MultipartFile
            return new MultipartFile() {
                @Override
                public String getName() {
                    return fileName;
                }

                @Override
                public String getOriginalFilename() {
                    return fileName;
                }

                @Override
                public String getContentType() {
                    return mimeType;
                }

                @Override
                public boolean isEmpty() {
                    return decodedBytes.length == 0;
                }

                @Override
                public long getSize() {
                    return decodedBytes.length;
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return decodedBytes;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return new ByteArrayInputStream(decodedBytes);
                }

                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {
                    try (FileOutputStream fos = new FileOutputStream(dest)) {
                        fos.write(decodedBytes);
                    }
                }
            };

        } catch (Exception e) {
        	log.error("error in base64ToMultipartFile:::::"+e.getMessage());
            throw new RuntimeException("Error converting base64 to MultipartFile", e);
        }
    }


}
