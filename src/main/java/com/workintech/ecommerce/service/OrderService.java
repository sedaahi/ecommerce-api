package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.request.OrderRequest;
import com.workintech.ecommerce.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    List<OrderResponse> getOrders(String email);

    OrderResponse createOrder(
            String email,
            OrderRequest request
    );
}