package com.mokasong.product.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
public class UpdateDetailCategoryRequest {
    @NotBlank(message = "not blank 여야 합니다.")
    private String name;

    @NotNull(message = "not null 이어야 합니다.")
    @Positive(message = "양수여야 합니다.")
    private Long rootCategoryId;
}
