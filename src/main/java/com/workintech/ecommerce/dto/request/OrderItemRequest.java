package com.workintech.ecommerce.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {

    @NotNull(message = "Product id is required.")
    @JsonProperty("product_id")
    private Long productId;

    @NotNull(message = "Product count is required.")
    @Min(value = 1, message = "Product count must be at least 1.")
    private Integer count;

    private String detail;
}