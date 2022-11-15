package com.mokasong.product.service;

import com.mokasong.common.dto.response.SuccessfulCreateResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.exception.custom.*;
import com.mokasong.common.util.AwsS3Client;
import com.mokasong.product.dto.request.admin.CreateProductRequest;
import com.mokasong.product.dto.request.admin.UpdateProductRequest;
import com.mokasong.product.dto.response.admin.ProductResponse;
import com.mokasong.product.dto.response.admin.ProductsResponse;
import com.mokasong.product.entity.Product;
import com.mokasong.product.entity.ProductDetailCategory;
import com.mokasong.product.entity.ProductImage;
import com.mokasong.product.entity.ProductRootCategory;
import com.mokasong.product.query.admin.ProductsCondition;
import com.mokasong.product.repository.AdminCategoryMapper;
import com.mokasong.product.repository.AdminProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

import static com.mokasong.common.util.ListHandler.notEmpty;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {
    private final AdminProductMapper productMapper;
    private final AdminCategoryMapper categoryMapper;
    private final AwsS3Client s3Client;

    @Override
    public SuccessfulCreateResponse createProduct(CreateProductRequest requestBody, List<MultipartFile> images) throws Exception {
        checkExistenceOfDetailCategory(requestBody.getDetailCategoryId(), 1);

        Product product = new Product(requestBody);
        productMapper.createProduct(product);

        if (notEmpty(images)) {
            if (images.size() > 20) {
                throw new PreconditionFailedException("상품 1개당 이미지는 최대 20개입니다.", 2);
            }

            productMapper.createProductImages(generateImages(images, product.getProductId(), 3));
        }

        return SuccessfulCreateResponse.builder()
                .success(true)
                .entityId(product.getProductId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long productId) throws Exception {
        ProductResponse.AdminPageProduct product = productMapper.getProductForAdminPage(productId);

        if (product == null) {
            throw new NotFoundException("상품이 존재하지 않습니다.", 1);
        }

        return ProductResponse.builder()
                .product(product)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductsResponse getProducts(ProductsCondition condition) throws Exception {
        ProductRootCategory rootCategory;
        ProductDetailCategory detailCategory;

        if (condition.rootCategoryIdIsNotNull()) {
            rootCategory = categoryMapper.getRootCategory(condition.getRootCategoryId());
            if (rootCategory == null) {
                throw new NotFoundException("선택한 최상위 카테고리가 존재하지 않습니다.", 1);
            }
        }

        if (condition.detailCategoryIdIsNotNull()) {
            detailCategory = checkExistenceOfDetailCategory(condition.getDetailCategoryId(), 2);

            if (!detailCategory.same(condition.getRootCategoryId())) {
                throw new ConflictException("상세 카테고리가 최상위 카테고리의 종류가 아닙니다.", 3);
            }
        }

        Long totalCount = productMapper.getTotalCountOfProductsByCondition(condition);
        Long totalPage = condition.extractTotalPage(totalCount);
        Long currentPage = condition.getPage();

        if (currentPage > totalPage) {
            throw new NotFoundException("유효하지 않은 페이지입니다.", 4);
        }

        List<ProductsResponse.Product> products = productMapper.getProductsByCondition(condition.extractBegin(), condition);

        return ProductsResponse.builder()
                .totalCount(totalCount)
                .totalPage(totalPage)
                .currentCount((long) products.size())
                .currentPage(currentPage)
                .products(products)
                .build();
    }

    @Override
    public SuccessfulResponse updateProduct(Long productId, UpdateProductRequest requestBody, List<MultipartFile> newImages) throws Exception {
        Product product = checkExistenceOfProduct(productId, 1);
        checkExistenceOfDetailCategory(requestBody.getDetailCategoryId(), 2);

        // products 테이블
        if (product.needToUpdate(requestBody)) {
            productMapper.updateProduct(product.update(requestBody));
        }

        // product_images 테이블
        // 삭제할 이미지를 추려 soft delete 한다.
        List<ProductImage> existingImages = productMapper.getImagesByProductId(productId);
        List<ProductImage> imagesToDelete = requestBody.getProductImagesToDelete(existingImages);

        if (notEmpty(imagesToDelete)) {
            productMapper.deleteImages(imagesToDelete);
        }

        // 추가할 이미지를 insert 한다.
        if (notEmpty(newImages)) {
            int totalCountOfImage = existingImages.size() - imagesToDelete.size() + newImages.size();

            if (totalCountOfImage > 20) {
                throw new PreconditionFailedException("상품 1개당 이미지는 최대 20개입니다.", 3);
            }

            productMapper.createProductImages(generateImages(newImages, productId, 4));
        }

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    @Override
    public SuccessfulResponse deleteProduct(Long productId) throws Exception {
        Product product = checkExistenceOfProduct(productId, 1);

        if (product.getIsDeleted()) {
            throw new ConflictException("이미 soft delete 되어있는 상품입니다.", 2);
        }

        // soft delete
        productMapper.deleteProduct(productId);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    @Override
    public SuccessfulResponse undeleteProduct(Long productId) throws Exception {
        Product product = checkExistenceOfProduct(productId, 1);

        if (!product.getIsDeleted()) {
            throw new ConflictException("soft delete 되어있는 상품이 아닙니다.", 2);
        }

        // soft delete 해제
        productMapper.undeleteProduct(productId);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    private Product checkExistenceOfProduct(Long productId, Integer errorCode) {
        // soft delete 무시하고 조회함
        Product product = productMapper.getProduct(productId);

        if (product == null) {
            throw new NotFoundException("상품이 존재하지 않습니다.", errorCode);
        }

        return product;
    }

    private ProductDetailCategory checkExistenceOfDetailCategory(Long detailCategoryId, Integer errorCode) {
        ProductDetailCategory detailCategory = categoryMapper.getDetailCategory(detailCategoryId);

        if (detailCategory == null) {
            throw new NotFoundException("선택한 상세 카테고리가 존재하지 않습니다.", errorCode);
        }

        return detailCategory;
    }

    private List<ProductImage> generateImages(List<MultipartFile> files, Long productId, Integer errorCode) throws Exception {
        List<ProductImage> images = new LinkedList<>();

        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();

            if (!originalName.contains(".")) {
                throw new PreconditionFailedException("파일의 확장자가 png 또는 jpg(jpeg)여야 합니다.", errorCode);
            }

            int lastDotIndex = file.getOriginalFilename().lastIndexOf('.');
            if (lastDotIndex == originalName.length() - 1) {
                throw new PreconditionFailedException("파일의 확장자가 png 또는 jpg(jpeg)여야 합니다.", errorCode);
            }

            String extension = originalName.substring(lastDotIndex + 1);
            if (!extension.equals("png") && !extension.equals("jpg") && !extension.equals("jpeg")) {
                throw new PreconditionFailedException("파일의 확장자가 png 또는 jpg(jpeg)여야 합니다.", errorCode);
            }

            String url = s3Client.uploadFile("images/products", file);
            images.add(new ProductImage(productId, url));
        }

        return images;
    }
}
