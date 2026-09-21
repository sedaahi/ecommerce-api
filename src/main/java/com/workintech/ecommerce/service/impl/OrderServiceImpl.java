package com.workintech.ecommerce.service.impl;

import com.workintech.ecommerce.dto.request.OrderItemRequest;
import com.workintech.ecommerce.dto.request.OrderRequest;
import com.workintech.ecommerce.dto.response.OrderItemResponse;
import com.workintech.ecommerce.dto.response.OrderResponse;
import com.workintech.ecommerce.entity.Address;
import com.workintech.ecommerce.entity.Order;
import com.workintech.ecommerce.entity.OrderItem;
import com.workintech.ecommerce.entity.Product;
import com.workintech.ecommerce.entity.User;
import com.workintech.ecommerce.exception.ApiException;
import com.workintech.ecommerce.repository.AddressRepository;
import com.workintech.ecommerce.repository.OrderRepository;
import com.workintech.ecommerce.repository.ProductRepository;
import com.workintech.ecommerce.repository.UserRepository;
import com.workintech.ecommerce.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserRepository userRepository,
            AddressRepository addressRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(String email) {

        return orderRepository
                .findAllByUserEmailOrderByOrderDateDesc(email)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse createOrder(
            String email,
            OrderRequest request
    ) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ApiException(
                                "User not found.",
                                HttpStatus.NOT_FOUND
                        )
                );

        // Adresin giriş yapan kullanıcıya ait olduğunu doğrular.
        Address address = addressRepository
                .findByIdAndUserEmail(
                        request.getAddressId(),
                        email
                )
                .orElseThrow(() ->
                        new ApiException(
                                "Address not found.",
                                HttpStatus.NOT_FOUND
                        )
                );

        validateCardExpiration(request);

        Order order = new Order();

        order.setOrderDate(request.getOrderDate());
        order.setUser(user);
        order.setAddress(address);

        order.setCardName(
                request.getCardName().trim()
        );

        order.setCardLastFour(
                getLastFourDigits(request.getCardNo())
        );

        order.setCardExpireMonth(
                request.getCardExpireMonth()
        );

        order.setCardExpireYear(
                request.getCardExpireYear()
        );

        BigDecimal productsTotal = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getProducts()) {

            Product product = productRepository
                    .findById(itemRequest.getProductId())
                    .orElseThrow(() ->
                            new ApiException(
                                    "Product not found: "
                                            + itemRequest.getProductId(),
                                    HttpStatus.NOT_FOUND
                            )
                    );

            if (product.getStock() < itemRequest.getCount()) {
                throw new ApiException(
                        "Insufficient stock for product: "
                                + product.getName(),
                        HttpStatus.BAD_REQUEST
                );
            }

            BigDecimal itemTotal = product
                    .getPrice()
                    .multiply(
                            BigDecimal.valueOf(
                                    itemRequest.getCount()
                            )
                    );

            productsTotal =
                    productsTotal.add(itemTotal);

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setCount(itemRequest.getCount());
            orderItem.setDetail(itemRequest.getDetail());

            // Sipariş anındaki fiyatı saklarız.
            orderItem.setUnitPrice(product.getPrice());

            orderItems.add(orderItem);
        }

        /*
         * Frontend tarafından gönderilen toplam fiyatı,
         * backend'in DB fiyatlarıyla hesapladığı toplamla karşılaştırır.
         */
        BigDecimal freeShippingLimit =
                new BigDecimal("150.00");

        BigDecimal shippingPayment =
                new BigDecimal("29.99");

        BigDecimal calculatedTotal = productsTotal;

// $150 ve üzerindeki siparişlerde kargo ücretsizdir.
        if (productsTotal.compareTo(freeShippingLimit) < 0) {
            calculatedTotal =
                    calculatedTotal.add(shippingPayment);
        }

// Para değerlerini 2 ondalık basamakta karşılaştırıyoruz.
        calculatedTotal =
                calculatedTotal.setScale(
                        2,
                        java.math.RoundingMode.HALF_UP
                );

        BigDecimal requestedPrice =
                request.getPrice().setScale(
                        2,
                        java.math.RoundingMode.HALF_UP
                );

        if (calculatedTotal.compareTo(requestedPrice) != 0) {
            throw new ApiException(
                    "Order price does not match calculated price.",
                    HttpStatus.BAD_REQUEST
            );
        }

        order.setPrice(calculatedTotal);
        order.setProducts(orderItems);

        /*
         * Tüm kontroller tamamlandıktan sonra stokları azaltıyoruz.
         */
        for (OrderItem orderItem : orderItems) {

            Product product = orderItem.getProduct();

            product.setStock(
                    product.getStock()
                            - orderItem.getCount()
            );

            productRepository.save(product);
        }

        Order savedOrder =
                orderRepository.save(order);

        return toResponse(savedOrder);
    }

    private void validateCardExpiration(
            OrderRequest request
    ) {

        YearMonth currentMonth = YearMonth.now();

        YearMonth expirationMonth;

        try {
            expirationMonth = YearMonth.of(
                    request.getCardExpireYear(),
                    request.getCardExpireMonth()
            );
        } catch (Exception exception) {
            throw new ApiException(
                    "Invalid card expiration date.",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (expirationMonth.isBefore(currentMonth)) {
            throw new ApiException(
                    "Card has expired.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private String getLastFourDigits(String cardNo) {

        String normalizedCardNo =
                cardNo.replaceAll("\\s+", "");

        if (normalizedCardNo.length() < 4) {
            throw new ApiException(
                    "Invalid card number.",
                    HttpStatus.BAD_REQUEST
            );
        }

        return normalizedCardNo.substring(
                normalizedCardNo.length() - 4
        );
    }

    private OrderResponse toResponse(Order order) {

        List<OrderItemResponse> products =
                order.getProducts()
                        .stream()
                        .map(item ->
                                new OrderItemResponse(
                                        item.getProduct().getId(),
                                        item.getProduct().getName(),
                                        item.getCount(),
                                        item.getDetail(),
                                        item.getUnitPrice()
                                )
                        )
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderDate(),
                order.getPrice(),
                order.getAddress().getId(),
                order.getCardName(),
                order.getCardLastFour(),
                order.getCardExpireMonth(),
                order.getCardExpireYear(),
                products
        );
    }
}