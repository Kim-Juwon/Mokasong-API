package com.mokasong.product.repository;

import com.mokasong.product.dto.response.admin.AllCategoriesResponse;
import com.mokasong.product.entity.Product;
import com.mokasong.product.entity.ProductDetailCategory;
import com.mokasong.product.entity.ProductRootCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminCategoryMapper {
    void createRootCategory(ProductRootCategory category);
    void createDetailCategory(ProductDetailCategory category);
    ProductRootCategory getRootCategory(Long rootCategoryId);
    ProductRootCategory getRootCategoryByName(String name);
    ProductDetailCategory getDetailCategoryByName(String name);
    List<AllCategoriesResponse.RootCategory> getAllRootCategoriesForAdminPage();
    void updateRootCategory(ProductRootCategory rootCategory);
    ProductDetailCategory getDetailCategory(Long detailCategoryId);
    void deleteRootCategory(Long rootCategoryId);
    void deleteDetailCategory(Long detailCategoryId);
    void updateDetailCategory();
    List<ProductDetailCategory> getDetailCategoriesIncludedInRootCategory(Long rootCategoryId);

    List<Product> getProductsIncludedInDetailCategory(Long detailCategoryId);
}
