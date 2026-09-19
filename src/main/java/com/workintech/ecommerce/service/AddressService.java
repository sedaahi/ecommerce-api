package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.request.AddressRequest;
import com.workintech.ecommerce.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {

    List<AddressResponse> getAddresses(String email);

    AddressResponse addAddress(
            String email,
            AddressRequest request
    );

    AddressResponse updateAddress(
            String email,
            AddressRequest request
    );

    void deleteAddress(
            String email,
            Long addressId
    );
}