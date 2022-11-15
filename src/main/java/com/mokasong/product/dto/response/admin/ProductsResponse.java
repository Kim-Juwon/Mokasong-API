package com.mokasong.product.dto.response.admin;

import com.mokasong.common.dto.response.PaginationResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter @SuperBuilder
public class ProductsResponse extends PaginationResponse {
    private List<Product> products;

    @Getter
    public final static class Product {
        private Long productId;
        private String name;
        private Integer price;
        private String rootCategoryName;
        private String detailCategoryName;
        private Integer stock;
        private Boolean isDeleted;
    }
}
