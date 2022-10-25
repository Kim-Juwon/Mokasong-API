package com.mokasong.product.repository;

import com.mokasong.product.dto.response.admin.ProductResponse;
import com.mokasong.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminProductMapper {
    ProductResponse.AdminPageProduct getProductForAdminPage(Long productId);
    Product getProduct(Long productId);
    void deleteProduct(Long productId);
    void undeleteProduct(Long productId);
}
