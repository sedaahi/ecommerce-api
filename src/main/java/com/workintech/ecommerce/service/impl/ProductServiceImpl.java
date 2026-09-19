package com.workintech.ecommerce.service.impl;

import com.workintech.ecommerce.dto.response.ProductImageResponse;
import com.workintech.ecommerce.dto.response.ProductListResponse;
import com.workintech.ecommerce.dto.response.ProductResponse;
import com.workintech.ecommerce.entity.Product;
import com.workintech.ecommerce.exception.ApiException;
import com.workintech.ecommerce.repository.ProductRepository;
import com.workintech.ecommerce.service.ProductService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(
            ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductListResponse getProducts(
            Integer limit,
            Integer offset,
            Long category,
            String filter,
            String sort
    ) {

        int safeLimit = limit == null || limit <= 0
                ? 25
                : limit;

        int safeOffset = offset == null || offset < 0
                ? 0
                : offset;

        Specification<Product> specification =
                createSpecification(category, filter);

        Sort sorting = createSort(sort);

        /*
         * Frontend offset gönderiyor.
         * Spring PageRequest ise page number bekliyor.
         *
         * offset=0,  limit=25 -> page=0
         * offset=25, limit=25 -> page=1
         */
        int pageNumber = safeOffset / safeLimit;

        Pageable pageable =
                PageRequest.of(
                        pageNumber,
                        safeLimit,
                        sorting
                );

        var page = productRepository.findAll(
                specification,
                pageable
        );

        List<ProductResponse> products =
                page.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return new ProductListResponse(
                products,
                page.getTotalElements()
        );
    }

    @Override
    public ProductResponse getProductById(Long productId) {

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new ApiException(
                                "Product not found.",
                                HttpStatus.NOT_FOUND
                        )
                );

        return toResponse(product);
    }

    private Specification<Product> createSpecification(
            Long category,
            String filter
    ) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates =
                    new ArrayList<>();

            if (category != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("category").get("id"),
                                category
                        )
                );
            }

            if (filter != null && !filter.isBlank()) {

                String search =
                        "%" + filter.trim().toLowerCase() + "%";

                Predicate namePredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("name")
                                ),
                                search
                        );

                Predicate descriptionPredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("description")
                                ),
                                search
                        );

                predicates.add(
                        criteriaBuilder.or(
                                namePredicate,
                                descriptionPredicate
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }

    private Sort createSort(String sort) {

        if (sort == null || sort.isBlank()) {
            return Sort.unsorted();
        }

        return switch (sort) {

            case "price:asc" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "price"
                    );

            case "price:desc" ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "price"
                    );

            case "rating:asc" ->
                    Sort.by(
                            Sort.Direction.ASC,
                            "rating"
                    );

            case "rating:desc" ->
                    Sort.by(
                            Sort.Direction.DESC,
                            "rating"
                    );

            default -> Sort.unsorted();
        };
    }

    private ProductResponse toResponse(Product product) {

        List<ProductImageResponse> images =
                product.getImages()
                        .stream()
                        .map(image ->
                                new ProductImageResponse(
                                        image.getUrl(),
                                        image.getImageIndex()
                                )
                        )
                        .toList();

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getRating(),
                product.getSellCount(),
                product.getCategory().getId(),
                images
        );
    }
}