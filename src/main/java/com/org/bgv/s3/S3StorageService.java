package com.org.bgv.s3;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final String bucketName = "bgv-doc-mahesh"; // 🔁 Replace this

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public Pair<String, String> uploadFile(MultipartFile file, String folder) {
        String key = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
            String url = "https://" + bucketName + ".s3.ap-south-1.amazonaws.com/" + key;
            return Pair.of(url, key);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload to S3", e);
        }
    }
    public void deleteFile(String key) {
        s3Client.deleteObject(builder -> builder.bucket(bucketName).key(key));
    }
}
