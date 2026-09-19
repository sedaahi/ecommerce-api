package com.workintech.ecommerce.controller;

import com.workintech.ecommerce.dto.request.AddressRequest;
import com.workintech.ecommerce.dto.response.AddressResponse;
import com.workintech.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAddresses(
            Authentication authentication
    ) {

        List<AddressResponse> addresses =
                addressService.getAddresses(authentication.getName());

        return ResponseEntity.ok(addresses);
    }

    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(
            @Valid @RequestBody AddressRequest request,
            Authentication authentication
    ) {

        AddressResponse response =
                addressService.addAddress(
                        authentication.getName(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping
    public ResponseEntity<AddressResponse> updateAddress(
            @Valid @RequestBody AddressRequest request,
            Authentication authentication
    ) {

        AddressResponse response =
                addressService.updateAddress(
                        authentication.getName(),
                        request
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId,
            Authentication authentication
    ) {

        addressService.deleteAddress(
                authentication.getName(),
                addressId
        );

        return ResponseEntity.noContent().build();
    }
}