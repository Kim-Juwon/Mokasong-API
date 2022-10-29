package com.mokasong.product.entity;

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

    public ProductDetailCategory(Long rootCategoryId, String name) {
        this.productRootCategoryId = rootCategoryId;
        setName(name);
    }

    public boolean isIncludedIn(Long rootCategoryId) {
        return productRootCategoryId.equals(rootCategoryId);
    }

    public boolean isSame(String name) {
        return this.name.equals(name);
    }

    public boolean isSame(Long productDetailCategoryId) {
        return this.productRootCategoryId.equals(productDetailCategoryId);
    }

    private void setName(String name) {
        this.name = name;
    }
}
