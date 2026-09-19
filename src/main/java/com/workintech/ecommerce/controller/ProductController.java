package com.workintech.ecommerce.controller;

import com.workintech.ecommerce.dto.response.ProductListResponse;
import com.workintech.ecommerce.dto.response.ProductResponse;
import com.workintech.ecommerce.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String sort
    ) {

        ProductListResponse response =
                productService.getProducts(
                        limit,
                        offset,
                        category,
                        filter,
                        sort
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long productId
    ) {

        ProductResponse response =
                productService.getProductById(productId);

        return ResponseEntity.ok(response);
    }
}