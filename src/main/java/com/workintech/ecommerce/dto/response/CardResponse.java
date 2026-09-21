package com.workintech.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CardResponse {

    private Long id;

    @JsonProperty("card_no")
    private String cardNo;

    @JsonProperty("expire_month")
    private Integer expireMonth;

    @JsonProperty("expire_year")
    private Integer expireYear;

    @JsonProperty("name_on_card")
    private String nameOnCard;
}