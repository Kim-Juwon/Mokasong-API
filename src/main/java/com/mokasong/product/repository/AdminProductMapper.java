package com.mokasong.product.repository;

import com.mokasong.product.dto.response.admin.ProductResponse;
import com.mokasong.product.dto.response.admin.ProductsResponse;
import com.mokasong.product.entity.Product;
import com.mokasong.product.entity.ProductImage;
import com.mokasong.product.query.admin.ProductsCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminProductMapper {
    // Create
    void createProduct(@Param("product") Product product);
    void createProductImages(@Param("productImages") List<ProductImage> productImages);

    // Read
    ProductResponse.AdminPageProduct getProductForAdminPage(@Param("productId") Long productId);
    Product getProduct(@Param("productId") Long productId);
    Long getTotalCountOfProductsByCondition(@Param("condition") ProductsCondition condition);
    List<ProductsResponse.Product> getProductsByCondition(@Param("begin") Long begin, @Param("condition") ProductsCondition condition);
    List<ProductImage> getImagesByProductId(@Param("productId") Long productId);

    // Update
    void updateProduct(@Param("product") Product product);

    // Delete
    void deleteProduct(@Param("productId") Long productId);
    void undeleteProduct(@Param("productId") Long productId);
    void deleteImages(@Param("imagesToDelete") List<ProductImage> imagesToDelete);
}
