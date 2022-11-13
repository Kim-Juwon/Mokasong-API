package com.mokasong.product.dto.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter @Setter
public class CreateProductRequest {
    @NotBlank
    private String name;

    @NotNull @Positive
    private Long detailCategoryId;

    @NotNull @Positive
    private Integer price;

    @Positive
    private Integer discountedPrice;

    @NotNull @Positive
    private Integer stock;

    public String toJson() throws Exception {
        return new ObjectMapper().writeValueAsString(this);
    }
}
