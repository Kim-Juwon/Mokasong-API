package com.mokasong.product.service;

import com.mokasong.common.dto.response.SuccessfulCreateResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.product.dto.request.admin.CreateProductRequest;
import com.mokasong.product.dto.request.admin.UpdateProductRequest;
import com.mokasong.product.dto.response.admin.ProductResponse;
import com.mokasong.product.dto.response.admin.ProductsResponse;
import com.mokasong.product.query.admin.ProductsCondition;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminProductService {
    SuccessfulCreateResponse createProduct(CreateProductRequest requestBody, List<MultipartFile> images) throws Exception;

    ProductResponse getProduct(Long productId) throws Exception;

    ProductsResponse getProducts(ProductsCondition condition) throws Exception;

    SuccessfulResponse updateProduct(Long productId, UpdateProductRequest requestBody, List<MultipartFile> newImages) throws Exception;

    SuccessfulResponse deleteProduct(Long productId) throws Exception;

    SuccessfulResponse undeleteProduct(Long productId) throws Exception;
}
