package com.mokasong.product.query.admin;

import com.mokasong.common.query.Pagination;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter @Setter
public class ProductsCondition implements Pagination {
    @NotNull
    @Positive
    private Long page;

    @NotNull
    @Positive
    private Long limit;

    private Boolean isDeleted;

    private Long rootCategoryId;

    private Long detailCategoryId;

    private Sort sort;

    private String searchString;

    @Getter
    private enum Sort {
        LATEST(1),
        CHEAP(2),
        EXPENSIVE(3);

        Sort(int value) {
            this.value = value;
        }

        final int value;
    }

    @Override
    public Long extractBegin() {
        return this.limit * this.page - this.limit;
    }

    /*
         totalPage(총 페이지 개수)는 (카테고리 총 개수 / 한 페이지에 존재할 카테고리 개수)를 올림한 값이다.
         만약 총 카테고리 총 개수가 0개일 경우, 페이지는 최소 1개라도 존재해야 하므로 총 페이지 개수를 1로 정한다.
     */
    @Override
    public Long extractTotalPage(Long totalCount) {
        return totalCount.equals(0L) ? 1 : (long) Math.ceil((double) totalCount / this.limit);
    }

    public boolean rootCategoryIdIsNotNull() {
        return this.rootCategoryId != null;
    }

    public boolean detailCategoryIdIsNotNull() {
        return this.detailCategoryId != null;
    }
}
