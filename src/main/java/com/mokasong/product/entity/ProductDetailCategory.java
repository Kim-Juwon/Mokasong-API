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
}
