package com.picketlogia.picket.api.product.service;

import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.product.model.ProductImage;
import com.picketlogia.picket.api.product.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final UploadService uploadService;

    /*
    * 1. 파일을 S3에 업로드 한다.
    * 2. 업로드 하고 S3에 저장된 파일 URL을 DB에 저장한다.
    * */
    public void upload(Product product, List<MultipartFile> files) throws SQLException, IOException {
        for (MultipartFile file : files) {

            // 파일을 서버에 저장
            String fileurl = null;    // 실제 파일 업로드(S3)
            fileurl = uploadService.upload(file); // 원본 파일명

            ProductImage productImage = ProductImage.builder()
                    .fileName(fileurl)  // 어떤 상품의 이미지인지 명시
                    .product(product)
                    .build();

            productImageRepository.save(productImage);  // 이미지 메타데이터 DB저장
        }
    }
}
