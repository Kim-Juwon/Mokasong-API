package com.mokasong.product.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
public class CreateCategoryRequest {
    @NotBlank(message = "name은 not blank입니다.")
    private String name;

    @NotNull(message = "type은 not null입니다.")
    private Type type;

    @Positive(message = "rootCategoryId는 1 이상입니다.")
    private Long rootCategoryId;

    @Getter
    private enum Type {
        ROOT,
        DETAIL
    }

    public boolean categoryIsRoot() {
        return this.type == Type.ROOT;
    }
}
