package com.mokasong.common.query;

public interface Pagination {
    Long extractBegin(); // MySQL LIMIT 구문에서의 시작 위치 추출
    Long extractTotalPage(Long totalCount); // 총 레코드 개수를 통해 총 페이지 수 추출
}
