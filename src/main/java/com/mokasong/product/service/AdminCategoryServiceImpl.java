package com.mokasong.product.service;

import com.mokasong.common.dto.response.SuccessfulCreateResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.exception.custom.ConflictException;
import com.mokasong.common.exception.custom.NotFoundException;
import com.mokasong.common.query.PageAndSearch;
import com.mokasong.product.dto.request.admin.CreateDetailCategoryRequest;
import com.mokasong.product.dto.request.admin.CreateRootCategoryRequest;
import com.mokasong.product.dto.request.admin.UpdateDetailCategoryRequest;
import com.mokasong.product.dto.request.admin.UpdateRootCategoryRequest;
import com.mokasong.product.dto.response.admin.AllCategoriesResponse;
import com.mokasong.product.dto.response.admin.DetailCategoriesResponse;
import com.mokasong.product.dto.response.admin.RootCategoriesResponse;
import com.mokasong.product.entity.ProductDetailCategory;
import com.mokasong.product.entity.ProductRootCategory;
import com.mokasong.product.repository.AdminCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final AdminCategoryMapper categoryMapper;

    @Override
    public SuccessfulCreateResponse createRootCategory(CreateRootCategoryRequest requestBody) throws Exception {
        if (categoryMapper.getRootCategoryByName(requestBody.getName()) != null) {
            throw new ConflictException("이름이 중복되는 카테고리가 존재합니다.", 1);
        }

        ProductRootCategory rootCategory = new ProductRootCategory(requestBody);
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
            throw new NotFoundException("최상위 카테고리가 존재하지 않습니다.", 1);
        }

        ProductDetailCategory sameNameDetailCategory =
                categoryMapper.getDetailCategoryByRootCategoryIdAndName(requestBody.getRootCategoryId(), requestBody.getName());

        if (sameNameDetailCategory != null) {
            throw new ConflictException("같은 최상위 카테고리에 이름이 중복되는 상세 카테고리가 존재합니다.", 2);
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
        Long totalPage = pageAndSearch.extractTotalPage(totalCount);
        Long currentPage = pageAndSearch.getPage();

        if (currentPage > totalPage) {
            throw new NotFoundException("유효하지 않은 페이지입니다.", 1);
        }

        List<RootCategoriesResponse.RootCategory> categories =
                categoryMapper.getRootCategoriesByCondition(pageAndSearch.extractBegin(), pageAndSearch);

        return RootCategoriesResponse.builder()
                .totalCount(totalCount)
                .totalPage(totalPage)
                .currentCount((long) categories.size())
                .currentPage(currentPage)
                .categories(categories)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DetailCategoriesResponse getDetailCategories(PageAndSearch pageAndSearch) throws Exception {
        Long totalCount = categoryMapper.getTotalCountOfDetailCategories();
        Long totalPage = pageAndSearch.extractTotalPage(totalCount);
        Long currentPage = pageAndSearch.getPage();

        if (currentPage > totalPage) {
            throw new NotFoundException("유효하지 않은 페이지입니다.", 1);
        }

        List<DetailCategoriesResponse.DetailCategory> categories =
                categoryMapper.getDetailCategoriesByCondition(pageAndSearch.extractBegin(), pageAndSearch);

        return DetailCategoriesResponse.builder()
                .totalCount(totalCount)
                .totalPage(totalPage)
                .currentCount((long) categories.size())
                .currentPage(currentPage)
                .categories(categories)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AllCategoriesResponse getAllCategories() throws Exception {
        List<AllCategoriesResponse.RootCategory> rootCategories = categoryMapper.getAllCategories();

        AllCategoriesResponse.AllCategories allCategories =
                AllCategoriesResponse.AllCategories.builder().rootCategories(rootCategories).build();

        return AllCategoriesResponse.builder()
                .categories(allCategories)
                .build();
    }

    @Override
    public SuccessfulResponse updateRootCategory(Long rootCategoryId, UpdateRootCategoryRequest requestBody) throws Exception {
        ProductRootCategory rootCategory = checkRootCategoryExists(rootCategoryId, 1);

        if (rootCategory.same(requestBody.getName())) {
            throw new ConflictException("이미 해당 이름으로 설정되어 있습니다.", 2);
        }

        ProductRootCategory sameNameRootCategory = categoryMapper.getRootCategoryByName(requestBody.getName());

        // 이름이 같은 최상위 카테고리가 조회되는데, id가 다르다면
        if (sameNameRootCategory != null && !sameNameRootCategory.sameEntity(rootCategory)) {
            throw new ConflictException("이름이 중복되는 카테고리가 존재합니다.", 3);
        }

        categoryMapper.updateRootCategory(rootCategory.update(requestBody));

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    @Override
    public SuccessfulResponse updateDetailCategory(Long rootCategoryId, Long detailCategoryId, UpdateDetailCategoryRequest requestBody) throws Exception {
        // 카테고리 존재여부 check
        checkRootCategoryExists(rootCategoryId, 1);
        ProductDetailCategory detailCategory = checkDetailCategoryExists(detailCategoryId, 2);

        // 포함관계 check
        if (!detailCategory.same(rootCategoryId)) {
            throw new ConflictException("상세 카테고리가 최상위 카테고리의 종류가 아닙니다.", 3);
        }

        // 새로 요청한 이름과 rootCategoryId가 이전과 같다면
        if (detailCategory.same(requestBody.getName()) && rootCategoryId.equals(requestBody.getNewRootCategoryId())) {
            throw new ConflictException("수정된 내용이 없습니다.", 4);
        }

        if (categoryMapper.getRootCategory(requestBody.getNewRootCategoryId()) == null) {
            throw new NotFoundException("새로 요청한 최상위 카테고리가 존재하지 않습니다.", 5);
        }

        ProductDetailCategory sameNameDetailCategory =
                categoryMapper.getDetailCategoryByRootCategoryIdAndName(requestBody.getNewRootCategoryId(), requestBody.getName());

        // 같은 최상위 카테고리의 상세 카테고리중 이름이 같은 레코드가 조회되고, id가 다르다면
        if (sameNameDetailCategory != null && !sameNameDetailCategory.sameEntity(detailCategory)) {
            throw new ConflictException("같은 최상위 카테고리에 이름이 중복되는 상세 카테고리가 존재합니다.", 6);
        }

        categoryMapper.updateDetailCategory(detailCategory.update(requestBody));

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    @Override
    public SuccessfulResponse deleteRootCategory(Long rootCategoryId) throws Exception {
        checkRootCategoryExists(rootCategoryId, 1);

        // 해당 최상위 카테고리에 포함된 상세 카테고리가 하나라도 존재하면 삭제할 수 없다.
        if (!categoryMapper.getDetailCategoriesIncludedInRootCategory(rootCategoryId).isEmpty()) {
            throw new ConflictException("해당 최상위 카테고리를 사용하는 상세 카테고리가 존재하기 때문에 삭제할 수 없습니다.", 2);
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

        if (!detailCategory.same(rootCategoryId)) {
            throw new ConflictException("상세 카테고리가 최상위 카테고리의 종류가 아닙니다.", 3);
        }

        // 해당 상세 카테고리에 포함된 상품이 하나라도 존재하면 삭제할 수 없다.
        if (!categoryMapper.getProductsIncludedInDetailCategory(detailCategoryId).isEmpty()) {
            throw new ConflictException("해당 카테고리를 사용하는 상품이 있기 때문에 삭제할 수 없습니다.", 4);
        }

        categoryMapper.deleteDetailCategory(detailCategoryId);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    private ProductRootCategory checkRootCategoryExists(Long rootCategoryId, Integer errorCode) throws Exception {
        ProductRootCategory category = categoryMapper.getRootCategory(rootCategoryId);

        if (category == null) {
            throw new NotFoundException("최상위 카테고리가 존재하지 않습니다.", errorCode);
        }

        return category;
    }

    private ProductDetailCategory checkDetailCategoryExists(Long detailCategoryId, Integer errorCode) throws Exception {
        ProductDetailCategory category = categoryMapper.getDetailCategory(detailCategoryId);

        if (category == null) {
            throw new NotFoundException("상세 카테고리가 존재하지 않습니다.", errorCode);
        }

        return category;
    }
}
