package com.picketlogia.picket.api.product.service;

import com.picketlogia.picket.api.product.utils.FileUploadUtil;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;


@Service
@RequiredArgsConstructor
public class S3UploadService implements UploadService {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String s3BucketName;

    // buildGradle에서 추가
    private final S3Operations s3Operations;

    @Override
    public String upload(MultipartFile file) throws SQLException, IOException {
        String dirPath = FileUploadUtil.makeUploadPath();

        // S3에 저장 후 S3 URL 반환
        S3Resource s3Resource = s3Operations.upload(
                s3BucketName,
                dirPath + file.getOriginalFilename(),
                file.getInputStream()
        );

        return s3Resource.getURL().toString();
    }
}
