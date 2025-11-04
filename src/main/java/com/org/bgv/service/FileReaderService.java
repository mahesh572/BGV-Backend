package com.org.bgv.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class FileReaderService {

    public String readFileFromClasspath(String filePath) {
        try {
            Resource resource = new ClassPathResource(filePath);
            InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }
    }

    public boolean fileExists(String filePath) {
        try {
            Resource resource = new ClassPathResource(filePath);
            return resource.exists();
        } catch (Exception e) {
            return false;
        }
    }
}