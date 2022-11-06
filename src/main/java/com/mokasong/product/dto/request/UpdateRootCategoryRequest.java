package com.mokasong.product.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
public class UpdateRootCategoryRequest {
    @NotBlank(message = "not blank 여야 합니다.")
    @Size(max = 15, message = "최대 15자입니다.")
    private String name;
}
