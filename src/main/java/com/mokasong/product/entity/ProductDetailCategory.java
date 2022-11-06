package com.mokasong.product.entity;

import com.mokasong.product.dto.request.CreateDetailCategoryRequest;
import com.mokasong.product.dto.request.UpdateDetailCategoryRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class ProductDetailCategory {
    private Long productDetailCategoryId;
    private Long productRootCategoryId;
    private String name;
    private Boolean isDeleted;
    private Date createdAt;
    private Date updatedAt;

    public ProductDetailCategory(CreateDetailCategoryRequest requestBody) {
        this.productRootCategoryId = requestBody.getRootCategoryId();
        this.setName(requestBody.getName());
    }

    public ProductDetailCategory update(UpdateDetailCategoryRequest requestBody) {
        this.productRootCategoryId = requestBody.getNewRootCategoryId();
        this.name = requestBody.getName();
        return this;
    }

    public boolean isKindOf(Long rootCategoryId) {
        return this.productRootCategoryId.equals(rootCategoryId);
    }

    public boolean isSame(Long productDetailCategoryId) {
        return this.productRootCategoryId.equals(productDetailCategoryId);
    }

    public boolean isSame(String name) {
        return this.name.equals(name);
    }

    private void setName(String name) {
        this.name = name;
    }
}
