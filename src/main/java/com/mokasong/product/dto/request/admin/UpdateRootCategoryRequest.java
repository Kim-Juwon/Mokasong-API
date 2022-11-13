package com.mokasong.product.dto.request.admin;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
public class UpdateRootCategoryRequest {
    @NotBlank
    @Size(max = 15)
    private String name;
}
