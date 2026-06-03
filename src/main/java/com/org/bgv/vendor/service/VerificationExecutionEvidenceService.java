package com.org.bgv.vendor.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.org.bgv.config.SecurityUtils;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.s3.S3StorageService;
import com.org.bgv.vendor.entity.VerificationMethodExecution;
import com.org.bgv.vendor.entity.VerificationMethodExecutionEvidence;
import com.org.bgv.vendor.evidence.dto.VerificationExecutionEvidenceDto;
import com.org.bgv.vendor.repository.VerificationMethodExecutionEvidenceRepository;
import com.org.bgv.vendor.repository.VerificationMethodExecutionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VerificationExecutionEvidenceService
         {

    private final VerificationMethodExecutionRepository executionRepository;
    private final VerificationCaseCheckRepository checkRepository;
    private final VerificationMethodExecutionEvidenceRepository evidenceRepository;
    private final S3StorageService fileStorageService;

   
    public List<VerificationExecutionEvidenceDto> uploadEvidence(
            Long executionId,
            Long checkId,
            List<MultipartFile> files,
            String notes) {

        VerificationMethodExecution execution =
                executionRepository.findById(executionId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Execution not found: " + executionId));

        VerificationCaseCheck check =
                checkRepository.findById(checkId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Check not found: " + checkId));

        List<VerificationMethodExecutionEvidence> savedEvidence =
                new ArrayList();

        for (MultipartFile file : files) {

        	Pair<String, String> uploadedfile =
                    fileStorageService.uploadFile(file,"vmethod_evidence");

            VerificationMethodExecutionEvidence evidence =
            		VerificationMethodExecutionEvidence.builder()
                            .methodExecution(execution)
                            .verificationCaseCheck(check)
                            .fileName(file.getOriginalFilename())
                            .fileUrl(uploadedfile.getFirst())
                            .key(uploadedfile.getSecond())
                            .contentType(file.getContentType())
                            .fileSize(file.getSize())
                            .notes(notes)
                            .uploadedAt(LocalDateTime.now())
                            .uploadedBy(SecurityUtils.getCurrentUserId())
                            .build();

            savedEvidence.add(
                    evidenceRepository.save(evidence)
            );
        }

        /*
         * Optional:
         * Add activity timeline entry
         */

        return savedEvidence.stream()
                .map(this::toDto)
                .toList();
    }
    
    
    
   
    public List<VerificationExecutionEvidenceDto> getExecutionEvidence(
            Long executionId) {

        VerificationMethodExecution execution =
                executionRepository.findById(executionId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Execution not found: "
                                                + executionId));

        return evidenceRepository
                .findByMethodExecutionExecutionId(execution.getExecutionId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    
   
    @Transactional
    public void deleteEvidence(Long evidenceId) {

        VerificationMethodExecutionEvidence evidence =
                evidenceRepository.findById(evidenceId)
                        .orElseThrow(() ->
                                new RuntimeException("Evidence not found: " + evidenceId)
                        );

        // Optional safety checks (recommended in real systems)
/*
        if (evidence.isLocked()) {
            throw new IllegalStateException("Cannot delete locked evidence");
        }
*/
        if (evidence.getMethodExecution() != null &&
                evidence.getMethodExecution().getStatus() == VerificationExecutionStatus.COMPLETED) {
            throw new IllegalStateException("Cannot delete evidence of completed execution");
        }

        // If you store files (S3/local), delete physical file first
        if(!deleteFileIfExists(evidence.getKey())) {
        	throw new BusinessException("Cannot delete evidence without Key");
        }

        // Delete DB record
        evidenceRepository.delete(evidence);
    }

    private boolean deleteFileIfExists(String key) {
        if (key == null || key.isBlank()) return false;

        // vmethod_evidence
        
        fileStorageService.deleteFile(key);
        return true;
        
    }
    
    
   

    private VerificationExecutionEvidenceDto toDto(
            VerificationMethodExecutionEvidence evidence) {

        return VerificationExecutionEvidenceDto.builder()
                .evidenceId(evidence.getEvidenceId())
                .fileName(evidence.getFileName())
                .fileUrl(evidence.getFileUrl())
                .contentType(evidence.getContentType())
                .fileSize(evidence.getFileSize())
                .notes(evidence.getNotes())
                .uploadedAt(evidence.getUploadedAt())
                .build();
    }
}
