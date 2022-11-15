package com.mokasong.product.entity;

import com.mokasong.product.dto.request.admin.CreateProductRequest;
import com.mokasong.product.dto.request.admin.UpdateProductRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class Product {
    private Long productId;
    private Long productDetailCategoryId;
    private String name;
    private Integer price;
    private Integer discountedPrice;
    private Integer stock;
    private Boolean isDeleted;
    private Date createdAt;
    private Date updatedAt;

    public Product(CreateProductRequest requestBody) {
        this.productDetailCategoryId = requestBody.getDetailCategoryId();
        this.name = requestBody.getName();
        this.price = requestBody.getPrice();
        this.discountedPrice = requestBody.getDiscountedPrice();
        this.stock = requestBody.getStock();
    }

    public Product update(UpdateProductRequest requestBody) {
        this.productDetailCategoryId = requestBody.getDetailCategoryId();
        this.name = requestBody.getName();
        this.price = requestBody.getPrice();
        this.discountedPrice = requestBody.getDiscountedPrice();
        this.stock = requestBody.getStock();
        return this;
    }

    // 하나라도 다른 컬럼이 있을시 true
    public boolean needToUpdate(UpdateProductRequest requestBody) {
        return !Objects.equals(this.productDetailCategoryId, requestBody.getDetailCategoryId())
                || !Objects.equals(this.name, requestBody.getName())
                || !Objects.equals(this.price, requestBody.getPrice())
                || !Objects.equals(this.discountedPrice, requestBody.getDiscountedPrice())
                || !Objects.equals(this.stock, requestBody.getStock());
    }
}
