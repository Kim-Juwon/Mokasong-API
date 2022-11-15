package com.mokasong.product.dto.request.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter @Setter
public class CreateProductRequest {
    @NotBlank
    @Size(max = 255)
    private String name;

    @NotNull
    private Long detailCategoryId;

    @NotNull
    @Positive
    private Integer price;

    @Positive
    private Integer discountedPrice;

    @NotNull
    @PositiveOrZero
    private Integer stock;

    public String toJson() throws Exception {
        return new ObjectMapper().writeValueAsString(this);
    }
}
