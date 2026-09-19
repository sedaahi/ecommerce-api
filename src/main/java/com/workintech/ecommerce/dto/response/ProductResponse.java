package com.workintech.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProductResponse {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private Double rating;

    /**JsonProperty
     * "sell_count": 125,
     * "category_id": 1
     */
    @JsonProperty("sell_count")
    private Integer sellCount;

    @JsonProperty("category_id")
    private Long categoryId;

    private List<ProductImageResponse> images;
}