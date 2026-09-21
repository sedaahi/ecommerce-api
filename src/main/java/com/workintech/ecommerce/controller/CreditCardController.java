package com.workintech.ecommerce.controller;

import com.workintech.ecommerce.dto.request.CardRequest;
import com.workintech.ecommerce.dto.response.CardResponse;
import com.workintech.ecommerce.service.CreditCardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/card")
public class CreditCardController {

    private final CreditCardService creditCardService;

    public CreditCardController(
            CreditCardService creditCardService
    ) {
        this.creditCardService = creditCardService;
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> getCards(
            Authentication authentication
    ) {

        List<CardResponse> cards =
                creditCardService.getCards(
                        authentication.getName()
                );

        return ResponseEntity.ok(cards);
    }

    @PostMapping
    public ResponseEntity<CardResponse> addCard(
            @Valid @RequestBody CardRequest request,
            Authentication authentication
    ) {

        CardResponse response =
                creditCardService.addCard(
                        authentication.getName(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping
    public ResponseEntity<CardResponse> updateCard(
            @Valid @RequestBody CardRequest request,
            Authentication authentication
    ) {

        CardResponse response =
                creditCardService.updateCard(
                        authentication.getName(),
                        request
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long cardId,
            Authentication authentication
    ) {

        creditCardService.deleteCard(
                authentication.getName(),
                cardId
        );

        return ResponseEntity.noContent().build();
    }
}