package com.mokasong.product.entity;

import com.mokasong.product.dto.request.admin.CreateDetailCategoryRequest;
import com.mokasong.product.dto.request.admin.UpdateDetailCategoryRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

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
        this.name = requestBody.getName();
    }

    public ProductDetailCategory update(UpdateDetailCategoryRequest requestBody) {
        this.productRootCategoryId = requestBody.getNewRootCategoryId();
        this.name = requestBody.getName();
        return this;
    }

    public boolean same(Long rootCategoryId) {
        return Objects.equals(this.productRootCategoryId, rootCategoryId);
    }

    public boolean same(String name) {
        return Objects.equals(this.name, name);
    }

    public boolean sameEntity(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof ProductDetailCategory)) {
            return false;
        }

        return this.productDetailCategoryId.equals(((ProductDetailCategory) object).getProductDetailCategoryId());
    }
}
