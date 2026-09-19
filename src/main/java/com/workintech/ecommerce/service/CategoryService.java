package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();
}