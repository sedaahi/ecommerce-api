package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.request.CardRequest;
import com.workintech.ecommerce.dto.response.CardResponse;

import java.util.List;

public interface CreditCardService {

    List<CardResponse> getCards(String email);

    CardResponse addCard(
            String email,
            CardRequest request
    );

    CardResponse updateCard(
            String email,
            CardRequest request
    );

    void deleteCard(
            String email,
            Long cardId
    );
}