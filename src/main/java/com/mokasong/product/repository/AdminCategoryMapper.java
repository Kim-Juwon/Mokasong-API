package com.mokasong.product.repository;

import com.mokasong.common.query.PageAndSearch;
import com.mokasong.product.dto.response.admin.AllCategoriesResponse;
import com.mokasong.product.dto.response.admin.DetailCategoriesResponse;
import com.mokasong.product.dto.response.admin.RootCategoriesResponse;
import com.mokasong.product.entity.Product;
import com.mokasong.product.entity.ProductDetailCategory;
import com.mokasong.product.entity.ProductRootCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminCategoryMapper {
    // Create
    void createRootCategory(@Param("category") ProductRootCategory category);
    void createDetailCategory(@Param("category") ProductDetailCategory category);

    // Read
    ProductRootCategory getRootCategory(@Param("rootCategoryId") Long rootCategoryId);
    ProductRootCategory getRootCategoryByName(@Param("name") String name);
    ProductDetailCategory getDetailCategoryByName(@Param("name") String name);
    ProductDetailCategory getDetailCategory(@Param("detailCategoryId") Long detailCategoryId);
    List<ProductDetailCategory> getDetailCategoriesIncludedInRootCategory(@Param("rootCategoryId") Long rootCategoryId);
    List<Product> getProductsIncludedInDetailCategory(@Param("detailCategoryId") Long detailCategoryId);
    Long getTotalCountOfRootCategories();
    Long getTotalCountOfDetailCategories();
    List<RootCategoriesResponse.RootCategory> getRootCategoriesByCondition(@Param("begin") Long begin, @Param("pageAndSearch") PageAndSearch pageAndSearch);
    List<DetailCategoriesResponse.DetailCategory> getDetailCategoriesByCondition(@Param("begin") Long begin, @Param("pageAndSearch") PageAndSearch pageAndSearch);
    List<AllCategoriesResponse.RootCategory> getAllCategories();

    // Update
    void updateRootCategory(@Param("rootCategory") ProductRootCategory rootCategory);
    void updateDetailCategory(@Param("detailCategory") ProductDetailCategory detailCategory);

    // Delete
    void deleteRootCategory(@Param("rootCategoryId") Long rootCategoryId); // soft delete
    void deleteDetailCategory(@Param("detailCategoryId") Long detailCategoryId); // soft delete
}
