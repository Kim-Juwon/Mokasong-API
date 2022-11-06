package com.mokasong.product.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter @Setter
public class CreateDetailCategoryRequest {
    @NotBlank(message = "not blank 이어야 합니다.")
    @Size(max = 15, message = "최대 15자입니다.")
    private String name;

    @NotNull(message = "not null 이어야 합니다.")
    @Positive(message = "1 이상이어야 합니다.")
    private Long rootCategoryId;
}
