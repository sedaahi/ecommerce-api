package com.workintech.ecommerce.service.impl;

import com.workintech.ecommerce.dto.request.CardRequest;
import com.workintech.ecommerce.dto.response.CardResponse;
import com.workintech.ecommerce.entity.CreditCard;
import com.workintech.ecommerce.entity.User;
import com.workintech.ecommerce.exception.ApiException;
import com.workintech.ecommerce.repository.CreditCardRepository;
import com.workintech.ecommerce.repository.UserRepository;
import com.workintech.ecommerce.service.CreditCardService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.DateTimeException;
import java.time.YearMonth;
@Service
public class CreditCardServiceImpl implements CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final UserRepository userRepository;

    public CreditCardServiceImpl(
            CreditCardRepository creditCardRepository,
            UserRepository userRepository
    ) {
        this.creditCardRepository = creditCardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CardResponse> getCards(String email) {

        return creditCardRepository
                .findAllByUserEmail(email)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CardResponse addCard(
            String email,
            CardRequest request
    ) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ApiException(
                                "User not found.",
                                HttpStatus.NOT_FOUND
                        )
                );

        CreditCard creditCard = new CreditCard();

        updateCardFields(creditCard, request);

        creditCard.setUser(user);

        CreditCard savedCard =
                creditCardRepository.save(creditCard);

        return toResponse(savedCard);
    }

    @Override
    @Transactional
    public CardResponse updateCard(
            String email,
            CardRequest request
    ) {

        if (request.getId() == null) {
            throw new ApiException(
                    "Card id is required.",
                    HttpStatus.BAD_REQUEST
            );
        }

        CreditCard creditCard = creditCardRepository
                .findByIdAndUserEmail(
                        request.getId(),
                        email
                )
                .orElseThrow(() ->
                        new ApiException(
                                "Card not found.",
                                HttpStatus.NOT_FOUND
                        )
                );

        updateCardFields(creditCard, request);

        CreditCard updatedCard =
                creditCardRepository.save(creditCard);

        return toResponse(updatedCard);
    }

    @Override
    @Transactional
    public void deleteCard(
            String email,
            Long cardId
    ) {

        CreditCard creditCard = creditCardRepository
                .findByIdAndUserEmail(
                        cardId,
                        email
                )
                .orElseThrow(() ->
                        new ApiException(
                                "Card not found.",
                                HttpStatus.NOT_FOUND
                        )
                );

        creditCardRepository.delete(creditCard);
    }
    private void validateExpirationDate(CardRequest request) {
        try {
            YearMonth expirationDate = YearMonth.of(
                    request.getExpireYear(),
                    request.getExpireMonth()
            );

            YearMonth currentDate = YearMonth.now();

            if (expirationDate.isBefore(currentDate)) {
                throw new ApiException(
                        "Card has expired.",
                        HttpStatus.BAD_REQUEST
                );
            }
        } catch (DateTimeException exception) {
            throw new ApiException(
                    "Invalid card expiration date.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void updateCardFields(
            CreditCard creditCard,
            CardRequest request
    ) {

        validateExpirationDate(request);

        creditCard.setCardNo(
                request.getCardNo().trim()
        );

        creditCard.setExpireMonth(
                request.getExpireMonth()
        );

        creditCard.setExpireYear(
                request.getExpireYear()
        );

        creditCard.setNameOnCard(
                request.getNameOnCard().trim()
        );
    }

    private CardResponse toResponse(
            CreditCard creditCard
    ) {

        return new CardResponse(
                creditCard.getId(),
                creditCard.getCardNo(),
                creditCard.getExpireMonth(),
                creditCard.getExpireYear(),
                creditCard.getNameOnCard()
        );
    }
}