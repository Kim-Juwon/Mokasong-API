package com.mokasong.product.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter @Builder
public class ProductResponse {
    private AdminPageProduct product;

    @Getter
    @JsonIgnoreProperties
    public static class AdminPageProduct {
        private Long productId;
        private String name;
        private Long rootCategoryId;
        private Long detailCategoryId;
        private Integer price;
        private Integer discountedPrice;
        private Integer stock;
        private List<String> imageUrls;
        private Boolean isDeleted;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date updatedAt;
    }
}
