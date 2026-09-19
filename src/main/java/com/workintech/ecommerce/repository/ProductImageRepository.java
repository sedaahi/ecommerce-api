package com.workintech.ecommerce.repository;

import com.workintech.ecommerce.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository
        extends JpaRepository<ProductImage, Long> {
}