package com.workintech.ecommerce.service.impl;

import com.workintech.ecommerce.dto.request.AddressRequest;
import com.workintech.ecommerce.dto.response.AddressResponse;
import com.workintech.ecommerce.entity.Address;
import com.workintech.ecommerce.entity.User;
import com.workintech.ecommerce.exception.ApiException;
import com.workintech.ecommerce.repository.AddressRepository;
import com.workintech.ecommerce.repository.UserRepository;
import com.workintech.ecommerce.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(
            AddressRepository addressRepository,
            UserRepository userRepository
    ) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<AddressResponse> getAddresses(String email) {

        return addressRepository
                .findAllByUserEmail(email)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AddressResponse addAddress(
            String email,
            AddressRequest request
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ApiException(
                                "User not found.",
                                HttpStatus.NOT_FOUND
                        )
                );

        Address address = new Address();

        updateAddressFields(address, request);

        address.setUser(user);

        Address savedAddress =
                addressRepository.save(address);

        return toResponse(savedAddress);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(
            String email,
            AddressRequest request
    ) {

        if (request.getId() == null) {
            throw new ApiException(
                    "Address id is required.",
                    HttpStatus.BAD_REQUEST
            );
        }

        Address address = addressRepository
                .findByIdAndUserEmail(
                        request.getId(),
                        email
                )
                .orElseThrow(() ->
                        new ApiException(
                                "Address not found.",
                                HttpStatus.NOT_FOUND
                        )
                );

        updateAddressFields(address, request);

        Address updatedAddress =
                addressRepository.save(address);

        return toResponse(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(
            String email,
            Long addressId
    ) {

        Address address = addressRepository
                .findByIdAndUserEmail(
                        addressId,
                        email
                )
                .orElseThrow(() ->
                        new ApiException(
                                "Address not found.",
                                HttpStatus.NOT_FOUND
                        )
                );

        addressRepository.delete(address);
    }

    private void updateAddressFields(
            Address address,
            AddressRequest request
    ) {

        address.setTitle(request.getTitle().trim());
        address.setName(request.getName().trim());
        address.setSurname(request.getSurname().trim());
        address.setPhone(request.getPhone().trim());
        address.setCity(request.getCity().trim());
        address.setDistrict(request.getDistrict().trim());
        address.setNeighborhood(
                request.getNeighborhood().trim()
        );
        address.setAddress(request.getAddress().trim());
    }

    private AddressResponse toResponse(Address address) {

        return new AddressResponse(
                address.getId(),
                address.getTitle(),
                address.getName(),
                address.getSurname(),
                address.getPhone(),
                address.getCity(),
                address.getDistrict(),
                address.getNeighborhood(),
                address.getAddress()
        );
    }
}