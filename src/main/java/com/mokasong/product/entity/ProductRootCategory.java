package com.mokasong.product.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class ProductRootCategory {
    private Long productRootCategoryId;
    private String name;
    private Boolean isDeleted;
    private Date createdAt;
    private Date updatedAt;

    public ProductRootCategory(String name) {
        this.setName(name);
    }

    public ProductRootCategory update(String name) {
        this.setName(name);
        return this;
    }

    public boolean isSame(String name) {
        return this.name.equals(name);
    }

    public boolean isSame(Long productRootCategoryId) {
        return this.productRootCategoryId.equals(productRootCategoryId);
    }

    private void setName(String name) {
        this.name = name;
    }
}
