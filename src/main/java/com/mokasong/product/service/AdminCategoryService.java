package com.mokasong.product.service;

import com.mokasong.common.dto.response.SuccessfulCreateResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.product.dto.request.CreateCategoryRequest;
import com.mokasong.product.dto.request.UpdateDetailCategoryRequest;
import com.mokasong.product.dto.request.UpdateRootCategoryRequest;
import com.mokasong.product.dto.response.admin.AllCategoriesResponse;

public interface AdminCategoryService {
    SuccessfulCreateResponse createCategory(CreateCategoryRequest requestBody) throws Exception;

    AllCategoriesResponse getAllCategories() throws Exception;

    SuccessfulResponse updateRootCategory(Long rootCategoryId, UpdateRootCategoryRequest requestBody) throws Exception;

    SuccessfulResponse updateDetailCategory(Long rootCategoryId, Long detailCategoryId, UpdateDetailCategoryRequest requestBody) throws Exception;

    SuccessfulResponse deleteRootCategory(Long rootCategoryId) throws Exception;

    SuccessfulResponse deleteDetailCategory(Long rootCategoryId, Long detailCategoryId) throws Exception;
}
