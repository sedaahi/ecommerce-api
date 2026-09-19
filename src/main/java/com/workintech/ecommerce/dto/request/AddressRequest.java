package com.workintech.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {

    private Long id;

    @NotBlank(message = "Address title is required.")
    @Size(max = 100, message = "Address title cannot exceed 100 characters.")
    private String title;

    @NotBlank(message = "Name is required.")
    @Size(max = 100, message = "Name cannot exceed 100 characters.")
    private String name;

    @NotBlank(message = "Surname is required.")
    @Size(max = 100, message = "Surname cannot exceed 100 characters.")
    private String surname;

    @NotBlank(message = "Phone is required.")
    @Pattern(
            regexp = "^0?5\\d{9}$",
            message = "Please enter a valid phone number."
    )
    private String phone;

    @NotBlank(message = "City is required.")
    private String city;

    @NotBlank(message = "District is required.")
    private String district;

    @NotBlank(message = "Neighborhood is required.")
    private String neighborhood;

    @NotBlank(message = "Address is required.")
    @Size(max = 500, message = "Address cannot exceed 500 characters.")
    private String address;
}