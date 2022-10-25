package com.mokasong.product.entity;

import com.mokasong.product.dto.request.CreateProductRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

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

    public Product create(CreateProductRequest requestBody) {
        this.productDetailCategoryId = requestBody.getProductDetailCategoryId();
        this.name = requestBody.getName();
        this.price = requestBody.getPrice();
        this.discountedPrice = requestBody.getDiscountedPrice();
        this.stock = requestBody.getStock();
        return this;
    }
}
