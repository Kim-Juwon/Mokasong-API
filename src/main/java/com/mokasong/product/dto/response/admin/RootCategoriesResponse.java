package com.mokasong.product.dto.response.admin;

import com.mokasong.common.dto.response.PaginationResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter @SuperBuilder
public class RootCategoriesResponse extends PaginationResponse {
    private List<RootCategory> categories;

    @Getter
    public static class RootCategory {
        private Long rootCategoryId;
        private String name;
    }
}
