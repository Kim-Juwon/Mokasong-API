package com.mokasong.common.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
public abstract class PaginationResponse {
    protected Long totalCount;
    protected Long totalPage;
    protected Long currentPage;
}
