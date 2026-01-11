package com.org.bgv.controller;

import java.util.List;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.config.JwtUtil;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.s3.S3StorageService;
import com.org.bgv.service.UserService;
import com.org.bgv.vendor.action.dto.VerificationActionRequest;
import com.org.bgv.vendor.dto.ActionLevel;
import com.org.bgv.vendor.dto.ActionReasonDTO;
import com.org.bgv.vendor.dto.ActionType;
import com.org.bgv.vendor.evidence.dto.EvidenceUploadRequest;
import com.org.bgv.vendor.evidence.dto.EvidenceUploadResponse;
import com.org.bgv.vendor.service.VerificationActionService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/vendor/action")
@RequiredArgsConstructor
@Slf4j
public class VendorActionController {

    private final VerificationActionService verificationActionService;
    private final S3StorageService s3StorageService;
    

    @GetMapping("/action-reasons")
    public ResponseEntity<CustomApiResponse<List<ActionReasonDTO>>> getActionReasons(
            @RequestParam Long categoryId,
            @RequestParam ActionType actionType,
            @RequestParam ActionLevel level) {

        log.info(
            "Fetching action reasons | categoryId={} | actionType={} | level={}",
            categoryId, actionType, level
        );

        List<ActionReasonDTO> reasons =
                verificationActionService.getReasons(categoryId, actionType, level);

        log.info(
            "Fetched {} action reasons | categoryId={} | actionType={} | level={}",
            reasons.size(), categoryId, actionType, level
        );

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Action reasons retrieved successfully",
                        reasons,
                        HttpStatus.OK
                )
        );
    }


    @PostMapping(value = "/evidence/upload", consumes = "multipart/form-data")
    public ResponseEntity<CustomApiResponse<EvidenceUploadResponse>> uploadEvidence(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long caseId,
            @RequestParam(required = false) Long checkId,
            @RequestParam(required = false) Long candidateId,
            @RequestParam(required = false) ActionType actionType,
            @RequestParam(required = false) ActionLevel actionLevel,
            @RequestParam(required = false) Long reasonId,
            @RequestParam(required = false) Long objectId,
            @RequestParam(required = false) Long docId) {

        Long userId = SecurityUtils.getCurrentUserId();
        
        

        log.info(
            "Evidence upload initiated | userId={} | caseId={} | checkId={} | actionType={} | actionLevel={}",
            userId, caseId, checkId, actionType, actionLevel
        );

        if (file.isEmpty()) {
            log.warn(
                "Evidence upload failed: empty file | userId={} | caseId={} | checkId={}",
                userId, caseId, checkId
            );
            return ResponseEntity.badRequest()
                    .body(CustomApiResponse.failure("File is empty", HttpStatus.BAD_REQUEST));
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            log.warn(
                "Evidence upload failed: file too large ({} bytes) | userId={} | caseId={}",
                file.getSize(), userId, caseId
            );
            return ResponseEntity.badRequest()
                    .body(CustomApiResponse.failure("File size exceeds 10MB", HttpStatus.BAD_REQUEST));
        }

        try {
            log.info(
                "Uploading evidence to S3 | fileName={} | size={} | userId={}",
                file.getOriginalFilename(), file.getSize(), userId
            );

            Pair<String, String> filePair =
                    s3StorageService.uploadFile(file, "EVIDENCE");

            log.info(
                "S3 upload successful | storageKey={} | userId={}",
                filePair.getSecond(), userId
            );

            EvidenceUploadResponse response =
                    verificationActionService.createUploadedEvidence(
                            EvidenceUploadRequest.builder()
                                    .fileName(file.getOriginalFilename())
                                    .fileUrl(filePair.getFirst())
                                    .fileType(file.getContentType())
                                    .fileSize(file.getSize())
                                    .storageKey(filePair.getSecond())
                                    .caseId(caseId)
                                    .checkId(checkId)
                                    .candidateId(candidateId)
                                    .actionType(actionType)
                                    .actionLevel(actionLevel)
                                    .reasonId(reasonId)
                                    .objectId(objectId)
                                    .docId(docId)
                                    .uploadedBy(userId)
                                    .build()
                    );

            log.info(
                "Evidence uploaded successfully | evidenceId={} | userId={} | caseId={}",
                response.getEvidenceId(), userId, caseId
            );

            return ResponseEntity.ok(
                    CustomApiResponse.success("File uploaded successfully", response, HttpStatus.OK)
            );

        } catch (Exception ex) {
            log.error(
                "Evidence upload failed | userId={} | caseId={} | checkId={}",
                userId, caseId, checkId, ex
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to upload evidence",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    @PostMapping("/request-info")
    public ResponseEntity<CustomApiResponse<?>> requestInfo(
            @RequestBody VerificationActionRequest request
    ) {

    	log.info("Request class = {}", request.getClass().getName());
    	
        log.info(
            "REQUEST_INFO initiated | caseId={} | checkId={} | level={} | reasonId={} | evidenceCount={} | remarks={}",
            request.getCaseId(),
            request.getCheckId(),
            request.getActionLevel(),
            request.getReasonId(),
            request.getEvidences() != null ? request.getEvidences().size() : 0,
            request.getRemarks()	
        );
        

        verificationActionService.createRequestInfoAction(request);

        log.info(
            "REQUEST_INFO successfully created | caseId={} | checkId={} | level={}",
            request.getCaseId(),
            request.getCheckId(),
            request.getActionLevel()
        );

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Request information raised successfully",
                        null,
                        HttpStatus.OK
                )
        );
    }

}

