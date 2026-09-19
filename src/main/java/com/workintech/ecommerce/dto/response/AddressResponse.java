package com.workintech.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddressResponse {

    private Long id;

    private String title;

    private String name;

    private String surname;

    private String phone;

    private String city;

    private String district;

    private String neighborhood;

    private String address;
}