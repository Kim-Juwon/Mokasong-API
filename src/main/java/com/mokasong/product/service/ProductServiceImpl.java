package com.mokasong.product.service;

import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.exception.custom.ConflictException;
import com.mokasong.common.exception.custom.NotFoundException;
import com.mokasong.product.dto.response.admin.ProductResponse;
import com.mokasong.product.entity.Product;
import com.mokasong.product.repository.AdminProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {
    private final AdminProductMapper adminProductMapper;

    public ProductServiceImpl(AdminProductMapper adminProductMapper) {
        this.adminProductMapper = adminProductMapper;
    }

    /*@Override
    @Transactional
    public SuccessfulCreateResponse createProductForAdmin(CreateProductRequest requestBody, List<MultipartFile> images) {
        ProductDetailCategory detailCategory =
                productRepository.getDetailCategoryById(requestBody.getProductDetailCategoryId());

        if (detailCategory == null) {
            throw new NotFoundException("productDetailCategoryId에 해당하는 카테고리 정보가 없습니다.", 1);
        }

        Long entityId = productRepository.createProduct(new Product().create(requestBody));

        return SuccessfulCreateResponse.builder()
                .entityId(12l)
                .build();
    }*/

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductForAdmin(Long productId) throws Exception {
        ProductResponse.AdminPageProduct product = adminProductMapper.getProductForAdminPage(productId);

        if (product == null) {
            throw new NotFoundException("없는 상품입니다.", 1);
        }

        return ProductResponse.builder()
                .product(product)
                .build();
    }

    @Override
    @Transactional
    public SuccessfulResponse deleteProductForAdmin(Long productId) throws Exception {
        Product product = checkProductExistsForAdmin(productId, 1);

        if (product.getIsDeleted()) {
            throw new ConflictException("이미 soft delete 되어있는 상품입니다.", 2);
        }

        // Soft delete
        adminProductMapper.deleteProduct(productId);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public SuccessfulResponse undeleteProductForAdmin(Long productId) throws Exception {
        Product product = checkProductExistsForAdmin(productId, 1);

        if (!product.getIsDeleted()) {
            throw new ConflictException("soft delete 되어있는 상품이 아닙니다.", 2);
        }

        // soft delete 해제
        adminProductMapper.undeleteProduct(productId);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    private Product checkProductExistsForAdmin(Long productId, Integer errorCode) {
        Product product = adminProductMapper.getProduct(productId);

        if (product == null) {
            throw new NotFoundException("없는 메뉴입니다.", errorCode);
        }

        return product;
    }
}
