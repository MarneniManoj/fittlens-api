package com.fittlens.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

@Service
public class S3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    public String generatePresignedUrl(String userId, String filename) {
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        String key = "uploads/" + userId + "/" + filename;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        URL presignedUrl = presigner.presignPutObject(presignRequest).url();
        presigner.close();
        return presignedUrl.toString();
    }
}