package com.workintech.ecommerce.controller;

import com.workintech.ecommerce.dto.request.OrderRequest;
import com.workintech.ecommerce.dto.response.OrderResponse;
import com.workintech.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(
            Authentication authentication
    ) {

        List<OrderResponse> orders =
                orderService.getOrders(
                        authentication.getName()
                );

        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication
    ) {

        OrderResponse response =
                orderService.createOrder(
                        authentication.getName(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}