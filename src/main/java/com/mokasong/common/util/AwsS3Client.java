package com.mokasong.common.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class AwsS3Client {
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.domain}")
    private String domain;

    public AwsS3Client(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadFile(String path, MultipartFile file) throws Exception {
        String today = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String realPath = new StringBuilder()
                .append(path)
                .append("/")
                .append(today)
                .toString();

        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf('.') + 1);
        String realName = new StringBuilder()
                .append(UUID.randomUUID())
                .append("-")
                .append(System.currentTimeMillis())
                .append(".")
                .append(extension)
                .toString();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        amazonS3.putObject(new PutObjectRequest("mokasong/" + realPath, realName, file.getInputStream(), metadata));

        return new StringBuilder()
                .append("https://")
                .append(domain)
                .append("/")
                .append(realPath)
                .append("/")
                .append(realName)
                .toString();
    }
}
