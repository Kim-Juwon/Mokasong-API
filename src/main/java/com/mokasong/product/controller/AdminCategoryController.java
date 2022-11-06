package com.mokasong.product.controller;

import com.mokasong.common.annotation.Auth;
import com.mokasong.common.dto.response.SuccessfulCreateResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.query.PageAndSearch;
import com.mokasong.product.dto.request.*;
import com.mokasong.product.dto.response.admin.AllCategoriesResponse;
import com.mokasong.product.dto.response.admin.DetailCategoriesResponse;
import com.mokasong.product.dto.response.admin.RootCategoriesResponse;
import com.mokasong.product.service.AdminCategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static com.mokasong.common.util.ControllerLayerUtils.getBaseURI;
import static com.mokasong.user.state.Authority.ADMIN;

@Tag(name = "Admin Product Category API", description = "상품 카테고리 API - 어드민")
@RestController
@Auth(ADMIN)
@RequestMapping("/admin/products/categories")
public class AdminCategoryController {
    private final AdminCategoryService adminCategoryService;

    @Value("${application.schema-and-host.current}")
    private String schemeAndHost;

    public AdminCategoryController(AdminCategoryService adminCategoryService) {
        this.adminCategoryService = adminCategoryService;
    }

    @Tag(name = "Admin Product Category API")
    @PostMapping("/root")
    @ApiOperation(value = "최상위 상품 카테고리 생성", notes = "최상위 상품 카테고리 생성", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulCreateResponse> createRootCategory(@RequestBody @Valid CreateRootCategoryRequest requestBody) throws Exception {
        SuccessfulCreateResponse responseBody = adminCategoryService.createRootCategory(requestBody);

        return ResponseEntity
                .created(URI.create(schemeAndHost + getBaseURI(this.getClass()) + "/" + responseBody.getEntityId()))
                .body(responseBody);
    }

    @Tag(name = "Admin Product Category API")
    @PostMapping("/detail")
    @ApiOperation(value = "상세 상품 카테고리 생성", notes = "상세 상품 카테고리 생성", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulCreateResponse> createDetailCategory(@RequestBody @Valid CreateDetailCategoryRequest requestBody) throws Exception {
        SuccessfulCreateResponse responseBody = adminCategoryService.createDetailCategory(requestBody);

        return ResponseEntity
                .created(URI.create(schemeAndHost + getBaseURI(this.getClass()) + "/" + responseBody.getEntityId()))
                .body(responseBody);
    }

    @Tag(name = "Admin Product Category API")
    @GetMapping("/root")
    @ApiOperation(value = "최상위 상품 카테고리 목록 조회 (페이지네이션)", notes = "페이지별 최상위 상품 카테고리 목록 조회", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<RootCategoriesResponse> getRootCategories(@Valid PageAndSearch pageAndSearch) throws Exception {
        RootCategoriesResponse responseBody = adminCategoryService.getRootCategories(pageAndSearch);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "Admin Product Category API")
    @GetMapping("/detail")
    @ApiOperation(value = "상세 상품 카테고리 목록 조회 (페이지네이션)", notes = "페이지별 상세 상품 카테고리 목록 조회", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<DetailCategoriesResponse> getDetailCategories(PageAndSearch pageAndSearch) throws Exception {
        DetailCategoriesResponse responseBody = adminCategoryService.getDetailCategories(pageAndSearch);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "Admin Product Category API")
    @GetMapping("")
    @ApiOperation(value = "상품 카테고리 전체 목록 조회", notes = "상품 카테고리 전체 목록 조회", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<AllCategoriesResponse> getAllCategories() throws Exception {
        AllCategoriesResponse responseBody = adminCategoryService.getAllCategories();

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "Admin Product Category API")
    @PutMapping("/root/{id}")
    @ApiOperation(value = "최상위 상품 카테고리 수정", notes = "최상위 상품 카테고리 수정")
    public ResponseEntity<SuccessfulResponse> updateRootCategory(
            @PathVariable("id") Long rootCategoryId, @RequestBody @Valid UpdateRootCategoryRequest requestBody) throws Exception {
        SuccessfulResponse responseBody = adminCategoryService.updateRootCategory(rootCategoryId, requestBody);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "Admin Product Category API")
    @PutMapping("/root/{rootCategoryId}/detail/{detailCategoryId}")
    @ApiOperation(value = "상세 상품 카테고리 수정", notes = "상세 상품 카테고리 수정")
    public ResponseEntity<SuccessfulResponse> updateDetailCategory(
            @PathVariable("rootCategoryId") Long rootCategoryId, @PathVariable("detailCategoryId") Long detailCategoryId,
            @RequestBody @Valid UpdateDetailCategoryRequest requestBody) throws Exception {
        SuccessfulResponse responseBody = adminCategoryService.updateDetailCategory(rootCategoryId, detailCategoryId, requestBody);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "Admin Product Category API")
    @DeleteMapping(value = "/root/{id}")
    @ApiOperation(value = "최상위 상품 카테고리 삭제", notes = "최상위 상품 카테고리 삭제")
    public ResponseEntity<SuccessfulResponse> deleteRootCategory(@PathVariable("id") Long rootCategoryId) throws Exception {
        SuccessfulResponse responseBody = adminCategoryService.deleteRootCategory(rootCategoryId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "Admin Product Category API")
    @DeleteMapping(value = "/root/{rootCategoryId}/detail/{detailCategoryId}")
    @ApiOperation(value = "상세 상품 카테고리 삭제", notes = "상세 상품 카테고리 삭제")
    public ResponseEntity<SuccessfulResponse> deleteDetailCategory(
            @PathVariable("rootCategoryId") Long rootCategoryId, @PathVariable("detailCategoryId") Long detailCategoryId) throws Exception {
        SuccessfulResponse responseBody = adminCategoryService.deleteDetailCategory(rootCategoryId, detailCategoryId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }
}
