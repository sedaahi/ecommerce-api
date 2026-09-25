package com.workintech.ecommerce.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardRequest {

    // PUT işleminde kullanılır, POST işleminde boş olabilir.
    private Long id;

    @NotBlank(message = "Card number is required.")
    @Pattern(
            regexp = "^\\d{16}$",
            message = "Card number must contain 16 digits."
    )
    @JsonProperty("card_no")
    private String cardNo;

    @NotNull(message = "Expire month is required.")
    @Min(value = 1, message = "Expire month must be between 1 and 12.")
    @Max(value = 12, message = "Expire month must be between 1 and 12.")
    @JsonProperty("expire_month")
    private Integer expireMonth;

    @NotNull(message = "Expire year is required.")
    @JsonProperty("expire_year")
    private Integer expireYear;

    @NotBlank(message = "Name on card is required.")
    @JsonProperty("name_on_card")
    private String nameOnCard;
}