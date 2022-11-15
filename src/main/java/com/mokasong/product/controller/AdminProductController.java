package com.mokasong.product.controller;

import com.mokasong.common.annotation.Auth;
import com.mokasong.common.dto.response.SuccessfulCreateResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.product.dto.request.admin.CreateProductRequest;
import com.mokasong.product.dto.request.admin.UpdateProductRequest;
import com.mokasong.product.dto.response.admin.ProductResponse;
import com.mokasong.product.dto.response.admin.ProductsResponse;
import com.mokasong.product.query.admin.ProductsCondition;
import com.mokasong.product.service.AdminProductService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.net.URI;
import java.util.List;

import static com.mokasong.user.state.Authority.ADMIN;

@Tag(name = "상품 (어드민)", description = "상품 API - 어드민")
@Auth(ADMIN)
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class AdminProductController {
    private final AdminProductService adminProductService;

    @Value("${application.schema-and-host.current}")
    private String schemeAndHost;

    @Tag(name = "상품 (어드민)")
    @PostMapping("")
    @ApiOperation(value = "상품 등록", notes = "상품 등록", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulCreateResponse> createProduct(
            @RequestPart("product") @Valid CreateProductRequest requestBody,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws Exception {
        SuccessfulCreateResponse responseBody = adminProductService.createProduct(requestBody, images);

        return ResponseEntity
                .created(URI.create(schemeAndHost + "/products/" + responseBody.getEntityId()))
                .body(responseBody);
    }

    @Tag(name = "상품 (어드민)")
    @GetMapping("/{id}")
    @ApiOperation(value = "상품 조회", notes = "상품 조회", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") Long productId) throws Exception {
        ProductResponse responseBody = adminProductService.getProduct(productId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "상품 (어드민)")
    @GetMapping("")
    @ApiOperation(value = "상품 목록 조회 (페이지네이션)", notes = "페이지별 상품 목록 조회", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<ProductsResponse> getProducts(@Valid ProductsCondition condition) throws Exception {
        ProductsResponse responseBody = adminProductService.getProducts(condition);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "상품 (어드민)")
    @PutMapping("/{id}")
    @ApiOperation(value = "상품 수정", notes = "상품 수정", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulResponse> updateProduct(
            @PathVariable("id") Long productId,
            @RequestPart("product") @Valid UpdateProductRequest requestBody,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) throws Exception {
        SuccessfulResponse responseBody = adminProductService.updateProduct(productId, requestBody, newImages);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "상품 (어드민)")
    @DeleteMapping("/{id}")
    @ApiOperation(value = "상품 삭제", notes = "상품 soft delete", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulResponse> deleteProduct(@PathVariable("id") Long productId) throws Exception {
        SuccessfulResponse responseBody = adminProductService.deleteProduct(productId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "상품 (어드민)")
    @PatchMapping("/{id}/undelete")
    @ApiOperation(value = "상품 삭제 해제", notes = "상품의 soft delete 해제", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulResponse> undeleteProduct(@PathVariable("id") Long productId) throws Exception {
        SuccessfulResponse responseBody = adminProductService.undeleteProduct(productId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }
}
