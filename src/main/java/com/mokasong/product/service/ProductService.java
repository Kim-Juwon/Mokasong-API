package com.mokasong.product.service;

import com.mokasong.common.dto.response.SuccessfulCreateResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.product.dto.request.CreateProductRequest;
import com.mokasong.product.dto.response.admin.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    //SuccessfulCreateResponse createProductForAdmin(CreateProductRequest requestBody, List<MultipartFile> images) throws Exception;
    ProductResponse getProductForAdmin(Long productId) throws Exception;
    SuccessfulResponse deleteProductForAdmin(Long productId) throws Exception;
    SuccessfulResponse undeleteProductForAdmin(Long productId) throws Exception;
}
