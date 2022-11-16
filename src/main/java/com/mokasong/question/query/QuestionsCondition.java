package com.mokasong.question.query;

import com.mokasong.common.query.Pagination;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter @Setter
public class QuestionsCondition implements Pagination {
    @NotNull
    @Positive
    private Long page;

    @NotNull
    @Positive
    private Long limit;

    private Boolean answered; // 답변 여부

    private String searchTitle; // 제목 검색

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
}
