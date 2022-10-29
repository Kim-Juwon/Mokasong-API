package com.mokasong.product.dto.response.admin;

import lombok.Getter;

import java.util.List;

@Getter
public class AllCategoriesResponse {
    private AdminPageAllCategories categories;

    @Getter
    private final static class AdminPageAllCategories {
        private final List<RootCategory> rootCategories;

        public AdminPageAllCategories(List<RootCategory> rootCategories) {
            this.rootCategories = rootCategories;
        }
    }

    @Getter
    public final static class RootCategory {
        private Long rootCategoryId;
        private String name;
        private List<DetailCategory> detailCategories;
    }

    @Getter
    private final static class DetailCategory {
        private Long detailCategoryId;
        private Long rootCategoryId;
        private String name;
    }

    public AllCategoriesResponse(List<RootCategory> rootCategories) {
        this.categories = new AdminPageAllCategories(rootCategories);
    }
}
