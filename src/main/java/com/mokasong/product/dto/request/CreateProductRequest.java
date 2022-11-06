package com.mokasong.product.dto.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Getter @Setter
public class CreateProductRequest {
    @NotBlank(message = "not blank 여야 합니다.")
    private String name;

    @NotNull(message = "not null 이어야 합니다.")
    private Long detailCategoryId;

    @Positive(message = "1 이상 2147483647 이하여야 합니다.")
    @NotNull(message = "not null 이어야 합니다.")
    private Integer price;

    @Positive(message = "1 이상 2147483647 이하여야 합니다.")
    private Integer discountedPrice;

    @Positive(message = "1 이상 2147483647 이하여야 합니다.")
    @NotNull(message = "not null 이어야 합니다.")
    private Integer stock;

    private List<MultipartFile> images;

    public CreateProductRequest addImages(List<MultipartFile> images) {
        this.images = images;
        return this;
    }

    public String toJson() throws Exception {
        return new ObjectMapper().writeValueAsString(this);
    }
}
