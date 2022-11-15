package com.mokasong.product.dto.request.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokasong.product.entity.ProductImage;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public class UpdateProductRequest {
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

    private List<String> imageUrlsToDelete;

    // 삭제할 이미지의 URL에 해당하는 ProductImage 엔티티 리스트를 반환
    public List<ProductImage> getProductImagesToDelete(List<ProductImage> existingProductImages) {
        List<ProductImage> result = new LinkedList<>();

        if (this.imageUrlsToDelete == null) {
            return result;
        }

        imageUrlsToDelete.forEach(imageUrlToDelete -> {
            existingProductImages.stream()
                    .filter(existingProductImage -> imageUrlToDelete.equals(existingProductImage.getUrl()))
                    .forEach(result::add);
        });

        return result;
    }

    public String toJson() throws Exception {
        return new ObjectMapper().writeValueAsString(this);
    }
}
