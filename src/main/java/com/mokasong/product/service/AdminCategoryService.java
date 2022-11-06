package com.mokasong.product.service;

import com.mokasong.common.dto.response.SuccessfulCreateResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.query.PageAndSearch;
import com.mokasong.product.dto.request.*;
import com.mokasong.product.dto.response.admin.AllCategoriesResponse;
import com.mokasong.product.dto.response.admin.DetailCategoriesResponse;
import com.mokasong.product.dto.response.admin.RootCategoriesResponse;

public interface AdminCategoryService {
    SuccessfulCreateResponse createRootCategory(CreateRootCategoryRequest requestBody) throws Exception;
    SuccessfulCreateResponse createDetailCategory(CreateDetailCategoryRequest requestBody) throws Exception;
    RootCategoriesResponse getRootCategories(PageAndSearch pageAndSearch) throws Exception;
    DetailCategoriesResponse getDetailCategories(PageAndSearch pageAndSearch) throws Exception;
    AllCategoriesResponse getAllCategories() throws Exception;
    SuccessfulResponse updateRootCategory(Long rootCategoryId, UpdateRootCategoryRequest requestBody) throws Exception;
    SuccessfulResponse updateDetailCategory(Long rootCategoryId, Long detailCategoryId, UpdateDetailCategoryRequest requestBody) throws Exception;
    SuccessfulResponse deleteRootCategory(Long rootCategoryId) throws Exception;
    SuccessfulResponse deleteDetailCategory(Long rootCategoryId, Long detailCategoryId) throws Exception;
}
