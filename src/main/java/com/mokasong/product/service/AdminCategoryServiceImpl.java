package com.mokasong.product.service;

import com.mokasong.common.dto.response.SuccessfulCreateResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.exception.custom.ConflictException;
import com.mokasong.common.exception.custom.InternalServerErrorException;
import com.mokasong.common.exception.custom.NotFoundException;
import com.mokasong.common.exception.custom.PreconditionFailedException;
import com.mokasong.common.query.PageAndSearch;
import com.mokasong.product.dto.request.*;
import com.mokasong.product.dto.response.admin.AllCategoriesResponse;
import com.mokasong.product.dto.response.admin.DetailCategoriesResponse;
import com.mokasong.product.dto.response.admin.RootCategoriesResponse;
import com.mokasong.product.entity.ProductDetailCategory;
import com.mokasong.product.entity.ProductRootCategory;
import com.mokasong.product.repository.AdminCategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mokasong.common.exception.ErrorCode.INTERNAL_SERVER_ERROR;

@Service
@Transactional
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final AdminCategoryMapper categoryMapper;

    public AdminCategoryServiceImpl(AdminCategoryMapper adminCategoryMapper) {
        this.categoryMapper = adminCategoryMapper;
    }

    @Override
    public SuccessfulCreateResponse createRootCategory(CreateRootCategoryRequest requestBody) throws Exception {
        if (categoryMapper.getRootCategoryByName(requestBody.getName()) != null) {
            throw new ConflictException("이름이 중복되는 카테고리가 존재합니다.", 1);
        }

        ProductRootCategory rootCategory = new ProductRootCategory(requestBody.getName());
        categoryMapper.createRootCategory(rootCategory);

        return SuccessfulCreateResponse.builder()
                .success(true)
                .entityId(rootCategory.getProductRootCategoryId())
                .build();
    }

    @Override
    public SuccessfulCreateResponse createDetailCategory(CreateDetailCategoryRequest requestBody) throws Exception {
        ProductRootCategory rootCategory = categoryMapper.getRootCategory(requestBody.getRootCategoryId());
        if (rootCategory == null) {
            throw new InternalServerErrorException("최상위 카테고리가 조회되지 않습니다.", INTERNAL_SERVER_ERROR.getErrorCode());
        }

        ProductDetailCategory sameNameDetailCategory = categoryMapper.getDetailCategoryByName(requestBody.getName());

        // 이름이 같은 상세 카테고리가 조회되는데, 소속된 최상위 카테고리까지 같다면
        if (sameNameDetailCategory != null && sameNameDetailCategory.isKindOf(requestBody.getRootCategoryId())) {
            throw new ConflictException(String.format("'%s' 카테고리에 이름이 중복되는 상세 카테고리가 존재합니다.", rootCategory.getName()), 1);
        }

        ProductDetailCategory newDetailCategory = new ProductDetailCategory(requestBody);
        categoryMapper.createDetailCategory(newDetailCategory);

        return SuccessfulCreateResponse.builder()
                .success(true)
                .entityId(newDetailCategory.getProductDetailCategoryId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RootCategoriesResponse getRootCategories(PageAndSearch pageAndSearch) throws Exception {
        Long totalCount = categoryMapper.getTotalCountOfRootCategories();
        Long totalPage = pageAndSearch.generateTotalPage(totalCount);
        Long currentPage = pageAndSearch.getPage();

        if (currentPage < 1 || currentPage > totalPage) {
            throw new NotFoundException("유효하지 않은 페이지입니다.", 1);
        }

        List<RootCategoriesResponse.RootCategory> categories =
                categoryMapper.getRootCategoriesByCondition(pageAndSearch.generateBegin(), pageAndSearch);

        return RootCategoriesResponse.builder()
                .totalCount(totalCount)
                .totalPage(totalPage)
                .currentPage(currentPage)
                .categories(categories)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DetailCategoriesResponse getDetailCategories(PageAndSearch pageAndSearch) throws Exception {
        Long totalCount = categoryMapper.getTotalCountOfDetailCategories();
        Long totalPage = pageAndSearch.generateTotalPage(totalCount);
        Long currentPage = pageAndSearch.getPage();

        if (currentPage < 1 || currentPage > totalPage) {
            throw new NotFoundException("유효하지 않은 페이지입니다.", 1);
        }

        List<DetailCategoriesResponse.DetailCategory> categories =
                categoryMapper.getDetailCategoriesByCondition(pageAndSearch.generateBegin(), pageAndSearch);

        return DetailCategoriesResponse.builder()
                .totalCount(totalCount)
                .totalPage(totalPage)
                .currentPage(currentPage)
                .categories(categories)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AllCategoriesResponse getAllCategories() throws Exception {
        List<AllCategoriesResponse.RootCategory> rootCategories = categoryMapper.getAllCategories();

        AllCategoriesResponse.AllCategories allCategories =
                AllCategoriesResponse.AllCategories.builder()
                        .rootCategories(rootCategories).build();

        return AllCategoriesResponse.builder()
                .categories(allCategories)
                .build();
    }

    @Override
    public SuccessfulResponse updateRootCategory(Long rootCategoryId, UpdateRootCategoryRequest requestBody) throws Exception {
        ProductRootCategory category = checkRootCategoryExists(rootCategoryId, 1);

        if (category.isSame(requestBody.getName())) {
            throw new ConflictException("이미 설정된 이름입니다.", 2);
        }

        ProductRootCategory sameNameRootCategory = categoryMapper.getRootCategoryByName(requestBody.getName());

        // 이름이 같은 최상위 카테고리가 조회되는데, id가 다르다면
        if (sameNameRootCategory != null && !sameNameRootCategory.isSame(rootCategoryId)) {
            throw new ConflictException("이름이 중복되는 카테고리가 존재합니다.", 3);
        }

        categoryMapper.updateRootCategory(category.update(requestBody.getName()));

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    @Override
    public SuccessfulResponse updateDetailCategory(Long rootCategoryId, Long detailCategoryId, UpdateDetailCategoryRequest requestBody) throws Exception {
        checkRootCategoryExists(rootCategoryId, 1);
        ProductDetailCategory detailCategory = checkDetailCategoryExists(detailCategoryId, 2);

        // path variable(rootCategoryId, detailCategoryId)에 대한 포함 관계 체크
        if (!detailCategory.isKindOf(rootCategoryId)) {
            throw new PreconditionFailedException("detailCategoryId에 해당하는 상세 카테고리가 rootCategoryId에 해당하는 최상위 카테고리에 포함되어 있지 않습니다.", 3);
        }

        // 새로 요청한 이름과 rootCategoryId가 이전과 같다면
        if (detailCategory.isSame(requestBody.getName()) && detailCategory.isKindOf(requestBody.getNewRootCategoryId())) {
            throw new ConflictException("요청에 수정된 내용이 없습니다.", 4);
        }

        ProductDetailCategory sameNameDetailCategory = categoryMapper.getDetailCategoryByName(requestBody.getName());

        // 이름이 같은 상세 카테고리가 조회되는데, id가 다르고 같은 최상위 카테고리 소속이라면
        if (sameNameDetailCategory != null && !sameNameDetailCategory.isSame(detailCategoryId) && sameNameDetailCategory.isKindOf(rootCategoryId)) {
            throw new ConflictException("이름이 중복되는 상세 카테고리가 존재합니다.", 5);
        }

        categoryMapper.updateDetailCategory(detailCategory.update(requestBody));

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    @Override
    public SuccessfulResponse deleteRootCategory(Long rootCategoryId) throws Exception {
        ProductRootCategory rootCategory = checkRootCategoryExists(rootCategoryId, 1);

        // 해당 최상위 카테고리에 포함된 상세 카테고리가 하나라도 존재하면 삭제할 수 없다.
        if (!categoryMapper.getDetailCategoriesIncludedInRootCategory(rootCategoryId).isEmpty()) {
            throw new PreconditionFailedException(
                    String.format("'%s' 카테고리를 사용하는 상세 카테고리가 있기 때문에 삭제할 수 없습니다.", rootCategory.getName()), 2
            );
        }

        categoryMapper.deleteRootCategory(rootCategoryId);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    @Override
    public SuccessfulResponse deleteDetailCategory(Long rootCategoryId, Long detailCategoryId) throws Exception {
        checkRootCategoryExists(rootCategoryId, 1);
        ProductDetailCategory detailCategory = checkDetailCategoryExists(detailCategoryId, 2);

        // path variable(rootCategoryId, detailCategoryId)에 대한 포함 관계 체크
        if (!detailCategory.isKindOf(rootCategoryId)) {
            throw new PreconditionFailedException("detailCategoryId에 해당하는 상세 카테고리가 rootCategoryId에 해당하는 최상위 카테고리에 포함되어 있지 않습니다.", 3);
        }

        // 해당 상세 카테고리에 포함된 상품이 하나라도 존재하면 삭제할 수 없다.
        if (!categoryMapper.getProductsIncludedInDetailCategory(detailCategoryId).isEmpty()) {
            throw new PreconditionFailedException("해당 카테고리를 사용하는 상품이 있기 떄문에 삭제할 수 없습니다.", 4);
        }

        categoryMapper.deleteDetailCategory(detailCategoryId);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    private ProductRootCategory checkRootCategoryExists(Long rootCategoryId, Integer errorCode) throws Exception {
        ProductRootCategory category = categoryMapper.getRootCategory(rootCategoryId);

        if (category == null) {
            throw new NotFoundException("없는 최상위 카테고리입니다.", errorCode);
        }

        return category;
    }

    private ProductDetailCategory checkDetailCategoryExists(Long detailCategoryId, Integer errorCode) throws Exception {
        ProductDetailCategory category = categoryMapper.getDetailCategory(detailCategoryId);

        if (category == null) {
            throw new NotFoundException("없는 상세 카테고리입니다.", errorCode);
        }

        return category;
    }
}
