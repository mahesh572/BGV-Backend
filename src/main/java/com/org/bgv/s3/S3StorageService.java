package com.org.bgv.s3;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.org.bgv.entity.Document;
import com.org.bgv.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final DocumentRepository documentRepository;

    private static final String BUCKET_NAME = "bgv-doc-mahesh";

    // -------------------- UPLOAD --------------------
    public Pair<String, String> uploadFile(MultipartFile file, String folder) {

        String key = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            String url = "https://" + BUCKET_NAME + ".s3.ap-south-1.amazonaws.com/" + key;
            return Pair.of(url, key);

        } catch (IOException e) {
            log.error("Failed to upload file to S3 | folder={}", folder, e);
            throw new RuntimeException("S3 upload failed", e);
        }
    }

    // -------------------- DELETE --------------------
    public void deleteFile(String key) {
        log.info("Deleting file from S3 | key={}", key);
        s3Client.deleteObject(b -> b.bucket(BUCKET_NAME).key(key));
    }

    // -------------------- DOWNLOAD --------------------
    public ResponseEntity<InputStreamResource> downloadFile(Long docId) {

        log.info("Downloading file from S3 | docId={}", docId);

        Document document = documentRepository.findById(docId)
                .orElseThrow(() ->
                        new RuntimeException("Document not found for id: " + docId));

        String key = document.getAwsDocKey();

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> s3Object =
                s3Client.getObject(request);

        GetObjectResponse metadata = s3Object.response();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + extractFileName(key) + "\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                metadata.contentType() != null
                                        ? metadata.contentType()
                                        : MediaType.APPLICATION_OCTET_STREAM_VALUE
                        )
                )
                .contentLength(metadata.contentLength())
                .body(new InputStreamResource(s3Object));
    }
    
 // -------------------- PRESIGNED URL (VIEW / DOWNLOAD) --------------------
    public String generatePresignedUrl(Long docId, int expiryMinutes, boolean inline) {

        Document document = documentRepository.findById(docId)
                .orElseThrow(() ->
                        new RuntimeException("Document not found for id: " + docId));

        String key = document.getAwsDocKey();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .responseContentDisposition(
                        inline
                                ? "inline; filename=\"" + extractFileName(key) + "\""
                                : "attachment; filename=\"" + extractFileName(key) + "\""
                )
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(expiryMinutes))
                        .getObjectRequest(getObjectRequest)
                        .build();

        PresignedGetObjectRequest presignedRequest =
                s3Presigner.presignGetObject(presignRequest);

        log.info("Generated presigned URL | docId={} | expiresIn={}min", docId, expiryMinutes);

        return presignedRequest.url().toString();
    }

    // -------------------- UTIL --------------------
    private String extractFileName(String key) {
        return key.substring(key.lastIndexOf('/') + 1);
    }
}
