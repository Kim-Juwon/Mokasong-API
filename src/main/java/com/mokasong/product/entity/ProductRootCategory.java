package com.mokasong.product.entity;

import com.mokasong.product.dto.request.admin.CreateRootCategoryRequest;
import com.mokasong.product.dto.request.admin.UpdateRootCategoryRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class ProductRootCategory {
    private Long productRootCategoryId;
    private String name;
    private Boolean isDeleted;
    private Date createdAt;
    private Date updatedAt;

    public ProductRootCategory(CreateRootCategoryRequest requestBody) {
        this.name = requestBody.getName();
    }

    public ProductRootCategory update(UpdateRootCategoryRequest requestBody) {
        this.name = requestBody.getName();
        return this;
    }

    public boolean same(String name) {
        return Objects.equals(this.name, name);
    }

    public boolean sameEntity(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof ProductRootCategory)) {
            return false;
        }

        return this.productRootCategoryId.equals(((ProductRootCategory) object).getProductRootCategoryId());
    }
}
