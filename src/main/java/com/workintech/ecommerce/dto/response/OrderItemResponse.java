package com.workintech.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class OrderItemResponse {

    @JsonProperty("product_id")
    private Long productId;

    private String name;

    private Integer count;

    private String detail;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;
}