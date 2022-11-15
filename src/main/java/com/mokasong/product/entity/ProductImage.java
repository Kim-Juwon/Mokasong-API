package com.mokasong.product.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class ProductImage {
    private Long productImageId;
    private Long productId;
    private String url;
    private Boolean isDeleted;
    private Date createdAt;
    private Date updatedAt;

    public ProductImage(Long productId, String url) {
        this.productId = productId;
        this.url = url;
    }
}
