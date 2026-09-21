package com.workintech.ecommerce.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderRequest {

    @NotNull(message = "Address id is required.")
    @JsonProperty("address_id")
    private Long addressId;

    @NotNull(message = "Order date is required.")
    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    @NotBlank(message = "Card number is required.")
    @Pattern(
            regexp = "^\\d{16}$",
            message = "Card number must contain 16 digits."
    )
    @JsonProperty("card_no")
    private String cardNo;

    @NotBlank(message = "Card holder name is required.")
    @JsonProperty("card_name")
    private String cardName;

    @NotNull(message = "Card expire month is required.")
    @Min(1)
    @Max(12)
    @JsonProperty("card_expire_month")
    private Integer cardExpireMonth;

    @NotNull(message = "Card expire year is required.")
    @JsonProperty("card_expire_year")
    private Integer cardExpireYear;

    @NotBlank(message = "CVV is required.")
    @Pattern(
            regexp = "^\\d{3}$",
            message = "CVV must contain 3 digits."
    )
    @JsonProperty("card_ccv")
    private String cardCcv;

    @NotNull(message = "Price is required.")
    @Positive(message = "Price must be greater than zero.")
    private BigDecimal price;

    @NotEmpty(message = "Order must contain at least one product.")
    @Valid
    private List<OrderItemRequest> products;
}