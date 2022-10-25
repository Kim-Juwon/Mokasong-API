package com.mokasong.product.controller;

import com.mokasong.common.annotation.Login;
import com.mokasong.common.dto.response.SuccessfulCreateResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.product.dto.request.CreateProductRequest;
import com.mokasong.product.dto.response.admin.ProductResponse;
import com.mokasong.product.service.ProductService;
import org.hibernate.validator.messageinterpolation.AbstractMessageInterpolator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static com.mokasong.common.util.ControllerLayerUtils.getBaseUrl;
import static com.mokasong.user.state.Authority.ADMIN;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {
    private final ProductService productService;

    @Value("${application.url.current}")
    private String host;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    /*@Login(ADMIN)
    @PostMapping("")
    public ResponseEntity<SuccessfulCreateResponse> createProduct(
            @RequestPart("product") @Valid CreateProductRequest requestBody,
            @RequestPart(value = "images") List<MultipartFile> images) throws Exception {

        SuccessfulCreateResponse response = productService.createProductForAdmin(requestBody, images);

        return ResponseEntity
                .created(URI.create(host + getBaseUrl(this.getClass()) + "/" + response.getEntityId()))
                .body(response);
    }*/

    @Login(ADMIN)
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") Long productId) throws Exception {
        ProductResponse responseBody = productService.getProductForAdmin(productId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Login(ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessfulResponse> deleteProduct(@PathVariable("id") Long productId) throws Exception {
        SuccessfulResponse responseBody = productService.deleteProductForAdmin(productId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Login(ADMIN)
    @PatchMapping("/{id}/undelete")
    public ResponseEntity<SuccessfulResponse> undeleteProduct(@PathVariable("id") Long productId) throws Exception {
        SuccessfulResponse responseBody = productService.undeleteProductForAdmin(productId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }
}
