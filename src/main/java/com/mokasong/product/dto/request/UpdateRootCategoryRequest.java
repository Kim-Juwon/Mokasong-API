package com.mokasong.product.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class UpdateRootCategoryRequest {
    @NotBlank(message = "name은 not blank입니다.")
    private String name;

    public boolean duplicate(String name) {
        return this.name.equals(name);
    }
}
