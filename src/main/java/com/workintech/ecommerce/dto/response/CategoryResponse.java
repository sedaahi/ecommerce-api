package com.workintech.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryResponse {

    private Long id;

    private String gender;

    private String code;

    private String title;

    private String img;

    private Double rating;
}