package com.mokasong.product.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
public class CreateProductRequest {
    @Size(min = 1, max = 20, message = "name은 1자 이상 20자 이하여야 합니다.")
    @NotBlank(message = "name은 필수입니다.")
    private String name;

    @NotBlank(message = "productDetailCategoryId는 필수입니다.")
    private Long productDetailCategoryId;

    @Positive(message = "price는 1 이상 2147483647 이하여야 합니다.")
    @NotBlank(message = "price는 필수입니다.")
    private Integer price;

    @Positive(message = "discountPrice는 양의 정수여야 합니다.")
    private Integer discountedPrice;

    @Positive(message = "stock은 1 이상 2147483647 이하여야 합니다.")
    @NotBlank(message = "stock은 필수입니다.")
    private Integer stock;
}
