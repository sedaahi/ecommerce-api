package com.workintech.ecommerce.repository;

import com.workintech.ecommerce.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findAllByUserEmail(String email);

    Optional<Address> findByIdAndUserEmail(
            Long id,
            String email
    );
}