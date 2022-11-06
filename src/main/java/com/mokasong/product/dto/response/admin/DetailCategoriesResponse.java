package com.mokasong.product.dto.response.admin;

import com.mokasong.common.dto.response.PaginationResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter @SuperBuilder
public class DetailCategoriesResponse extends PaginationResponse {
    private List<DetailCategory> categories;

    @Getter
    public static class DetailCategory {
        private Long detailCategoryId;
        private Long rootCategoryId;
        private String name;
    }
}
