package com.mokasong.common.query;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter @Setter
public class PageAndSearch {
    @NotNull @Positive
    private Long page;
    @NotNull @Positive
    private Long limit;

    private String searchString;

    // MySQL LIMIT 구문에서의 시작 위치
    public Long generateBegin() {
        return this.limit * this.page - this.limit;
    }

    /*
         totalPage(총 페이지 개수)는 (카테고리 총 개수 / 한 페이지에 존재할 카테고리 개수)를 올림한 값이다.
         만약 총 카테고리 총 개수가 0개일 경우, 페이지는 최소 1개라도 존재해야 하므로 총 페이지 개수를 1로 정한다.
     */
    public Long generateTotalPage(Long totalCount) {
        return totalCount.equals((long) 0) ? 1 : (long) Math.ceil((double) totalCount / this.limit);
    }
}
