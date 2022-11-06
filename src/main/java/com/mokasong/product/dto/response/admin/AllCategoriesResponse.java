package com.mokasong.product.dto.response.admin;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class AllCategoriesResponse {
    private AllCategories categories;

    @Getter @Builder
    public static class AllCategories {
        private List<RootCategory> rootCategories;
    }

    @Getter
    public static class RootCategory {
        private Long rootCategoryId;
        private String name;
        private List<DetailCategory> detailCategories;
    }

    @Getter
    private static class DetailCategory {
        private Long detailCategoryId;
        private Long rootCategoryId;
        private String name;
    }
}
