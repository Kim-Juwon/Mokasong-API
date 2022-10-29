package com.mokasong.product.service;

import com.mokasong.common.dto.response.SuccessfulCreateResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.exception.CriticalException;
import com.mokasong.common.exception.custom.ConflictException;
import com.mokasong.common.exception.custom.NotFoundException;
import com.mokasong.common.exception.custom.PreconditionFailedException;
import com.mokasong.common.exception.custom.UnprocessableEntityException;
import com.mokasong.product.dto.request.CreateCategoryRequest;
import com.mokasong.product.dto.request.UpdateDetailCategoryRequest;
import com.mokasong.product.dto.request.UpdateRootCategoryRequest;
import com.mokasong.product.dto.response.admin.AllCategoriesResponse;
import com.mokasong.product.entity.ProductDetailCategory;
import com.mokasong.product.entity.ProductRootCategory;
import com.mokasong.product.repository.AdminCategoryMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mokasong.common.exception.ErrorCode.INTERNAL_SERVER_ERROR;

@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final AdminCategoryMapper adminCategoryMapper;

    public AdminCategoryServiceImpl(AdminCategoryMapper adminCategoryMapper) {
        this.adminCategoryMapper = adminCategoryMapper;
    }

    @Override
    @Transactional
    public SuccessfulCreateResponse createCategory(CreateCategoryRequest requestBody) throws Exception {
        Long entityId;

        if (requestBody.categoryIsRoot()) {
            if (adminCategoryMapper.getRootCategoryByName(requestBody.getName()) != null) {
                throw new ConflictException("이름이 중복되는 카테고리가 존재합니다.", 1);
            }

            ProductRootCategory rootCategory = new ProductRootCategory(requestBody.getName());
            adminCategoryMapper.createRootCategory(rootCategory);

            entityId = rootCategory.getProductRootCategoryId();
        } else {
            if (requestBody.getRootCategoryId() == null) {
                throw new UnprocessableEntityException("type이 DETAIL이면 rootCategoryId는 필수입니다.", "rootCategoryId", requestBody.getRootCategoryId());
            }

            if (adminCategoryMapper.getRootCategory(requestBody.getRootCategoryId()) == null) {
                throw new CriticalException("productRootCategoryId로 product가 조회되지 않음.", INTERNAL_SERVER_ERROR.getErrorCode(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            ProductDetailCategory existing = adminCategoryMapper.getDetailCategoryByName(requestBody.getName());
            if (existing != null && detailIsIncludedInRoot(existing, requestBody.getRootCategoryId())) {
                throw new ConflictException("같은 소속 카테고리중에 이름이 중복되는 카테고리가 있습니다.", 2);
            }

            ProductDetailCategory detailCategory = new ProductDetailCategory(requestBody.getRootCategoryId(), requestBody.getName());
            adminCategoryMapper.createDetailCategory(detailCategory);

            entityId = detailCategory.getProductDetailCategoryId();
        }

        return SuccessfulCreateResponse.builder()
                .success(true)
                .entityId(entityId)
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public AllCategoriesResponse getAllCategories() throws Exception {
        List<AllCategoriesResponse.RootCategory> rootCategories = adminCategoryMapper.getAllRootCategoriesForAdminPage();

        return new AllCategoriesResponse(rootCategories);
    }

    @Override
    @Transactional
    public SuccessfulResponse updateRootCategory(Long rootCategoryId, UpdateRootCategoryRequest requestBody) throws Exception {
        ProductRootCategory category = checkRootCategoryExists(rootCategoryId, 1);

        if (category.isSame(requestBody.getName())) {
            throw new ConflictException("이미 설정된 이름입니다.", 2);
        }

        ProductRootCategory sameName = adminCategoryMapper.getRootCategoryByName(requestBody.getName());

        if (sameName != null && !sameName.isSame(rootCategoryId)) {
            throw new ConflictException("이름이 중복되는 카테고리가 존재합니다.", 3);
        }

        adminCategoryMapper.updateRootCategory(category.update(requestBody.getName()));

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public SuccessfulResponse updateDetailCategory(Long rootCategoryId, Long detailCategoryId, UpdateDetailCategoryRequest requestBody) throws Exception {
        checkRootCategoryExists(rootCategoryId, 1);
        ProductDetailCategory detailCategory = checkDetailCategoryExists(detailCategoryId, 2);

        if (!detailIsIncludedInRoot(detailCategory, rootCategoryId)) {
            throw new PreconditionFailedException("상세 카테고리가 최상위 카테고리에 포함되지 않습니다.", 3);
        }

        if (detailCategory.isSame(requestBody.getName())) {
            throw new ConflictException("이미 설정된 이름입니다.", 4);
        }

        ProductDetailCategory sameName = adminCategoryMapper.getDetailCategoryByName(requestBody.getName());

        if (sameName != null && !sameName.isSame(detailCategoryId)) {
            throw new ConflictException("이름이 중복되는 상세 카테고리가 이미 존재합니다.", 5);
        }

        adminCategoryMapper.updateDetailCategory();

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public SuccessfulResponse deleteRootCategory(Long rootCategoryId) throws Exception {
        checkRootCategoryExists(rootCategoryId, 1);

        if (!adminCategoryMapper.getDetailCategoriesIncludedInRootCategory(rootCategoryId).isEmpty()) {
            throw new PreconditionFailedException("해당 카테고리를 사용하는 상세 카테고리가 있기 때문에 삭제할 수 없습니다.", 1);
        }

        adminCategoryMapper.deleteRootCategory(rootCategoryId);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public SuccessfulResponse deleteDetailCategory(Long rootCategoryId, Long detailCategoryId) throws Exception {
        checkRootCategoryExists(rootCategoryId, 1);
        ProductDetailCategory detailCategory = checkDetailCategoryExists(detailCategoryId, 2);

        if (!detailIsIncludedInRoot(detailCategory, rootCategoryId)) {
            throw new PreconditionFailedException("상세 카테고리가 최상위 카테고리에 포함되지 않습니다.", 3);
        }

        if (!adminCategoryMapper.getProductsIncludedInDetailCategory(detailCategoryId).isEmpty()) {
            throw new PreconditionFailedException("해당 카테고리를 사용하는 상품이 있기 떄문에 삭제할 수 없습니다.", 4);
        }

        adminCategoryMapper.deleteDetailCategory(detailCategoryId);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    private ProductRootCategory checkRootCategoryExists(Long rootCategoryId, Integer errorCode) throws Exception {
        ProductRootCategory category = adminCategoryMapper.getRootCategory(rootCategoryId);

        if (category == null) {
            throw new NotFoundException("없는 최상위 카테고리입니다.", errorCode);
        }

        return category;
    }

    private ProductDetailCategory checkDetailCategoryExists(Long detailCategoryId, Integer errorCode) throws Exception {
        ProductDetailCategory category = adminCategoryMapper.getDetailCategory(detailCategoryId);

        if (category == null) {
            throw new NotFoundException("없는 상세 카테고리입니다.", errorCode);
        }

        return category;
    }

    private boolean detailIsIncludedInRoot(ProductDetailCategory detailCategory, Long rootCategoryId) throws Exception {
        return detailCategory.isIncludedIn(rootCategoryId);
    }
}
