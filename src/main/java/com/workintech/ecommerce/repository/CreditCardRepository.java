package com.workintech.ecommerce.repository;

import com.workintech.ecommerce.entity.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditCardRepository
        extends JpaRepository<CreditCard, Long> {

    List<CreditCard> findAllByUserEmail(String email);

    Optional<CreditCard> findByIdAndUserEmail(
            Long id,
            String email
    );
}