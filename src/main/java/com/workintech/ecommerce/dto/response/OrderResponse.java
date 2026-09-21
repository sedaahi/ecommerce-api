package com.workintech.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderResponse {

    private Long id;

    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    private BigDecimal price;

    @JsonProperty("address_id")
    private Long addressId;

    @JsonProperty("card_name")
    private String cardName;

    @JsonProperty("card_last_four")
    private String cardLastFour;

    @JsonProperty("card_expire_month")
    private Integer cardExpireMonth;

    @JsonProperty("card_expire_year")
    private Integer cardExpireYear;

    private List<OrderItemResponse> products;
}