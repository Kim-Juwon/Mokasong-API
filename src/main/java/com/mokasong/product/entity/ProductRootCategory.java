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
}
