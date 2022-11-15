package com.mokasong.product.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter @Builder
public class ProductResponse {
    private AdminPageProduct product;

    @Getter
    public final static class AdminPageProduct {
        private Long productId;
        private String name;
        private Integer price;
        private Integer discountedPrice;
        private Integer stock;
        private Boolean isDeleted;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date updatedAt;
        private RootCategory rootCategory;
        private DetailCategory detailCategory;
        private List<String> imageUrls;
    }

    @Getter
    private final static class RootCategory {
        private Long rootCategoryId;
        private String name;
    }

    @Getter
    private final static class DetailCategory {
        private Long detailCategoryId;
        private Long rootCategoryId;
        private String name;
    }
}
