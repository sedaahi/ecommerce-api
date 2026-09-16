package com.workintech.ecommerce.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreRequest {

    @NotBlank(message = "Store name is required.")
    @Size(min = 3, message = "Store name must be at least 3 characters.")
    private String name;

    @NotBlank(message = "Phone is required.")
    @Pattern(
            regexp = "^0?5\\d{9}$",
            message = "Please enter a valid phone number."
    )
    private String phone;

    @JsonProperty("tax_no")
    @NotBlank(message = "Tax number is required.")
    @Pattern(
            regexp = "^T\\d{4}V\\d{6}$",
            message = "Please enter a valid tax number."
    )
    private String taxNo;

    @JsonProperty("bank_account")
    @NotBlank(message = "Bank account is required.")
    @Pattern(
            regexp = "^TR\\d{24}$",
            message = "Please enter a valid IBAN."
    )
    private String bankAccount;
}