package com.mokasong.product.controller;

import com.mokasong.common.annotation.Auth;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.product.dto.response.admin.ProductResponse;
import com.mokasong.product.service.AdminProductService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mokasong.user.state.Authority.ADMIN;

@RestController
@RequestMapping("/admin/products")
@Tag(name = "Admin Product API", description = "상품 API - 어드민")
@Auth(ADMIN)
public class AdminProductController {
    private final AdminProductService adminProductService;

    @Value("${application.schema-and-host.current}")
    private String schemeAndHost;

    public AdminProductController(AdminProductService adminProductService) {
        this.adminProductService = adminProductService;
    }

    @Tag(name = "Admin Product API")
    @GetMapping("/{id}")
    @ApiOperation(value = "상품 조회", notes = "상품 조회", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") Long productId) throws Exception {
        ProductResponse responseBody = adminProductService.getProduct(productId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "Admin Product API")
    @DeleteMapping("/{id}")
    @ApiOperation(value = "상품 삭제", notes = "상품 soft delete", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulResponse> deleteProduct(@PathVariable("id") Long productId) throws Exception {
        SuccessfulResponse responseBody = adminProductService.deleteProduct(productId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "Admin Product API")
    @PatchMapping("/{id}/undelete")
    @ApiOperation(value = "상품 삭제 해제", notes = "상품의 soft delete 해제", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulResponse> undeleteProduct(@PathVariable("id") Long productId) throws Exception {
        SuccessfulResponse responseBody = adminProductService.undeleteProduct(productId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }
}
