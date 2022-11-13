package com.mokasong.product.controller;

import com.mokasong.common.annotation.Auth;
import com.mokasong.common.dto.response.SuccessfulCreateResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.query.PageAndSearch;
import com.mokasong.product.dto.request.admin.CreateDetailCategoryRequest;
import com.mokasong.product.dto.request.admin.CreateRootCategoryRequest;
import com.mokasong.product.dto.request.admin.UpdateDetailCategoryRequest;
import com.mokasong.product.dto.request.admin.UpdateRootCategoryRequest;
import com.mokasong.product.dto.response.admin.AllCategoriesResponse;
import com.mokasong.product.dto.response.admin.DetailCategoriesResponse;
import com.mokasong.product.dto.response.admin.RootCategoriesResponse;
import com.mokasong.product.service.AdminCategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.mokasong.user.state.Authority.ADMIN;

@Tag(name = "상품 카테고리 (어드민)", description = "상품 카테고리 API - 어드민")
@RestController
@RequestMapping("/admin/products/categories")
@Auth(ADMIN)
public class AdminCategoryController {
    private final AdminCategoryService adminCategoryService;

    public AdminCategoryController(AdminCategoryService adminCategoryService) {
        this.adminCategoryService = adminCategoryService;
    }

    @Tag(name = "상품 카테고리 (어드민)")
    @PostMapping("/root")
    @ApiOperation(value = "최상위 상품 카테고리 생성", notes = "최상위 상품 카테고리 생성", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulCreateResponse> createRootCategory(@RequestBody @Valid CreateRootCategoryRequest requestBody) throws Exception {
        SuccessfulCreateResponse responseBody = adminCategoryService.createRootCategory(requestBody);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "상품 카테고리 (어드민)")
    @PostMapping("/detail")
    @ApiOperation(value = "상세 상품 카테고리 생성", notes = "상세 상품 카테고리 생성", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulCreateResponse> createDetailCategory(@RequestBody @Valid CreateDetailCategoryRequest requestBody) throws Exception {
        SuccessfulCreateResponse responseBody = adminCategoryService.createDetailCategory(requestBody);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "상품 카테고리 (어드민)")
    @GetMapping("/root")
    @ApiOperation(value = "최상위 상품 카테고리 목록 조회 (페이지네이션)", notes = "최상위 상품 카테고리 목록 조회 (페이지네이션)", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<RootCategoriesResponse> getRootCategories(@Valid PageAndSearch pageAndSearch) throws Exception {
        RootCategoriesResponse responseBody = adminCategoryService.getRootCategories(pageAndSearch);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "상품 카테고리 (어드민)")
    @GetMapping("/detail")
    @ApiOperation(value = "상세 상품 카테고리 목록 조회 (페이지네이션)", notes = "상세 상품 카테고리 목록 조회 (페이지네이션)", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<DetailCategoriesResponse> getDetailCategories(@Valid PageAndSearch pageAndSearch) throws Exception {
        DetailCategoriesResponse responseBody = adminCategoryService.getDetailCategories(pageAndSearch);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "상품 카테고리 (어드민)")
    @GetMapping("")
    @ApiOperation(value = "모든 상품 카테고리 조회", notes = "모든 최상위 카테고리 및 상세 카테고리 조회", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<AllCategoriesResponse> getAllCategories() throws Exception {
        AllCategoriesResponse responseBody = adminCategoryService.getAllCategories();

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "상품 카테고리 (어드민)")
    @PutMapping("/root/{id}")
    @ApiOperation(value = "최상위 상품 카테고리 수정", notes = "최상위 상품 카테고리 수정", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulResponse> updateRootCategory(
            @PathVariable("id") Long rootCategoryId, @RequestBody @Valid UpdateRootCategoryRequest requestBody) throws Exception {
        SuccessfulResponse responseBody = adminCategoryService.updateRootCategory(rootCategoryId, requestBody);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "상품 카테고리 (어드민)")
    @PutMapping("/root/{rootCategoryId}/detail/{detailCategoryId}")
    @ApiOperation(value = "상세 상품 카테고리 수정", notes = "상세 상품 카테고리 수정", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulResponse> updateDetailCategory(
            @PathVariable("rootCategoryId") Long rootCategoryId, @PathVariable("detailCategoryId") Long detailCategoryId,
            @RequestBody @Valid UpdateDetailCategoryRequest requestBody) throws Exception {
        SuccessfulResponse responseBody = adminCategoryService.updateDetailCategory(rootCategoryId, detailCategoryId, requestBody);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "상품 카테고리 (어드민)")
    @DeleteMapping(value = "/root/{id}")
    @ApiOperation(value = "최상위 상품 카테고리 삭제", notes = "최상위 상품 카테고리 삭제", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulResponse> deleteRootCategory(@PathVariable("id") Long rootCategoryId) throws Exception {
        SuccessfulResponse responseBody = adminCategoryService.deleteRootCategory(rootCategoryId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "상품 카테고리 (어드민)")
    @DeleteMapping(value = "/root/{rootCategoryId}/detail/{detailCategoryId}")
    @ApiOperation(value = "상세 상품 카테고리 삭제", notes = "상세 상품 카테고리 삭제", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulResponse> deleteDetailCategory(
            @PathVariable("rootCategoryId") Long rootCategoryId, @PathVariable("detailCategoryId") Long detailCategoryId) throws Exception {
        SuccessfulResponse responseBody = adminCategoryService.deleteDetailCategory(rootCategoryId, detailCategoryId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }
}
