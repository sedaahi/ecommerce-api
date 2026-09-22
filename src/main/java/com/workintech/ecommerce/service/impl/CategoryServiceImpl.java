package com.workintech.ecommerce.service.impl;

import com.workintech.ecommerce.dto.response.CategoryResponse;
import com.workintech.ecommerce.entity.Category;
import com.workintech.ecommerce.repository.CategoryRepository;
import com.workintech.ecommerce.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CategoryResponse toResponse(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getGender(),
                category.getCode(),
                category.getTitle(),
                category.getImg(),
                category.getRating()
        );
    }
}