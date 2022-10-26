package com.mokasong.product.service;

import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.product.dto.response.admin.ProductResponse;

public interface AdminProductService {
    ProductResponse getProduct(Long productId) throws Exception;
    SuccessfulResponse deleteProduct(Long productId) throws Exception;
    SuccessfulResponse undeleteProduct(Long productId) throws Exception;
}
