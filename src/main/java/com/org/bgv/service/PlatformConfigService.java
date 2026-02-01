package com.org.bgv.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.org.bgv.dto.PlatformConfigResponse;
import com.org.bgv.dto.PlatformConfigSaveRequest;
import com.org.bgv.entity.PlatformConfig;
import com.org.bgv.repository.PlatformConfigRepository;
import com.org.bgv.s3.S3StorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlatformConfigService {

    private final PlatformConfigRepository platformConfigRepository;
    private final S3StorageService s3StorageService; 

    @Cacheable(value = "platformConfig", key = "'default'")
    public PlatformConfig get() {
        return platformConfigRepository.findById(1L)
                .orElseThrow(() ->
                        new IllegalStateException("PlatformConfig not initialized"));
    }

    @CacheEvict(value = "platformConfig", allEntries = true)
    public PlatformConfig update(PlatformConfig config) {
        return platformConfigRepository.save(config);
    }
    
    
    @Transactional
    public void uploadPlatformLogo(MultipartFile file) {

    	 PlatformConfig config = platformConfigRepository.findById(1L)
    	            .orElseThrow(() -> new RuntimeException("Platform config not found"));

        validateLogo(file);

        if (config.getPlatformLogoKey() != null) {
            s3StorageService.deleteFile(config.getPlatformLogoKey());
        }

        String folderName = "platform/logo";

        Pair<String, String> upload =
                s3StorageService.uploadFile(file, folderName);

        config.setPlatformLogoKey(upload.getSecond());
        config.setPlatformLogoUrl(upload.getFirst());
        config.setUpdatedAt(LocalDateTime.now());

        platformConfigRepository.save(config);
    }

    
    private void validateLogo(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        List<String> allowedTypes = List.of("image/png", "image/jpeg", "image/webp");

        if (!allowedTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("Invalid file type");
        }

        long maxSize = 2 * 1024 * 1024; // 2MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File too large");
        }
    }

    
    @Transactional
    public PlatformConfigResponse updatePlatformConfig(
            PlatformConfigSaveRequest request
    ) {
        PlatformConfig config = platformConfigRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Platform config not found"));

        updateEntity(config, request);

        config.setUpdatedAt(LocalDateTime.now());

        return toResponse(
                platformConfigRepository.save(config)
        );
    }
    
    @Transactional
    public PlatformConfigResponse getPlatformConfig() {
    	
    	 PlatformConfig config = platformConfigRepository.findById(1L)
                 .orElseThrow(() -> new RuntimeException("Platform config not found"));
    	 return toResponse(
                 platformConfigRepository.save(config)
         );
    }
    
    
    
    public static PlatformConfigResponse toResponse(PlatformConfig config) {
        return PlatformConfigResponse.builder()
                .id(config.getId())
                .platformBrandName(config.getPlatformBrandName())
                .platformLegalName(config.getPlatformLegalName())
                .websiteUrl(config.getWebsiteUrl())
                .platformLogoUrl(config.getPlatformLogoUrl())
               // .logoUploadedAt(config.getLogoUploadedAt())
                .supportEmail(config.getSupportEmail())
                .defaultFromName(config.getDefaultFromName())
                .defaultFromEmail(config.getDefaultFromEmail())
               // .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }

    public static void updateEntity(
            PlatformConfig config,
            PlatformConfigSaveRequest request
    ) {
        config.setPlatformBrandName(request.getPlatformBrandName());
        config.setPlatformLegalName(request.getPlatformLegalName());
        config.setWebsiteUrl(request.getWebsiteUrl());
        config.setSupportEmail(request.getSupportEmail());
        config.setDefaultFromName(request.getDefaultFromName());
        config.setDefaultFromEmail(request.getDefaultFromEmail());
    }
    
}