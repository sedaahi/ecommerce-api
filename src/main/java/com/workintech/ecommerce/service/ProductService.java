package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.response.ProductListResponse;
import com.workintech.ecommerce.dto.response.ProductResponse;

public interface ProductService {

    ProductListResponse getProducts(
            Integer limit,
            Integer offset,
            Long category,
            String filter,
            String sort
    );

    ProductResponse getProductById(Long productId);
}