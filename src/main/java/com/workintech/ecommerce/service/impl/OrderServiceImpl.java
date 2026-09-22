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
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal FREE_SHIPPING_LIMIT =
            new BigDecimal("150.00");

    private static final BigDecimal SHIPPING_PAYMENT =
            new BigDecimal("29.99");

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

        // 1. Giriş yapan kullanıcıyı bul.
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ApiException(
                                "User not found.",
                                HttpStatus.NOT_FOUND
                        )
                );

        // 2. Adresin giriş yapan kullanıcıya ait olduğunu doğrula.
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

        // 3. Kartın son kullanma tarihini kontrol et.
        validateCardExpiration(request);

        // 4. Order nesnesini hazırla.
        Order order = new Order();

        order.setOrderDate(request.getOrderDate());
        order.setUser(user);
        order.setAddress(address);

        order.setCardName(
                request.getCardName().trim()
        );

        // Tam kart numarasını Order tablosunda saklamıyoruz.
        order.setCardLastFour(
                getLastFourDigits(request.getCardNo())
        );

        order.setCardExpireMonth(
                request.getCardExpireMonth()
        );

        order.setCardExpireYear(
                request.getCardExpireYear()
        );

        /*
         * CVV burada bilerek Order entity'sine aktarılmıyor.
         * CVV veritabanında saklanmamalıdır.
         */

        BigDecimal productsTotal = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();

        // 5. Sepetteki ürünleri kontrol et.
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

            // Stok kontrolü.
            if (product.getStock() < itemRequest.getCount()) {
                throw new ApiException(
                        "Insufficient stock for product: "
                                + product.getName(),
                        HttpStatus.BAD_REQUEST
                );
            }

            // Fiyat frontend'den değil DB'deki üründen alınır.
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

            // Sipariş anındaki ürün fiyatını sakla.
            orderItem.setUnitPrice(product.getPrice());

            orderItems.add(orderItem);
        }

        // 6. Backend tarafında gerçek sipariş toplamını hesapla.
        BigDecimal calculatedTotal =
                calculateGrandTotal(productsTotal);

        BigDecimal requestedPrice =
                request.getPrice().setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        /*
         * Frontend'den gelen toplam ile backend'in hesapladığı
         * toplam aynı değilse siparişi kabul etme.
         */
        if (calculatedTotal.compareTo(requestedPrice) != 0) {
            throw new ApiException(
                    "Order price does not match calculated price.",
                    HttpStatus.BAD_REQUEST
            );
        }

        order.setPrice(calculatedTotal);
        order.setProducts(orderItems);

        // 7. Tüm kontroller başarılıysa stokları azalt.
        for (OrderItem orderItem : orderItems) {

            Product product = orderItem.getProduct();

            product.setStock(
                    product.getStock()
                            - orderItem.getCount()
            );

            productRepository.save(product);
        }

        // 8. Cascade sayesinde OrderItem'lar da kaydedilir.
        Order savedOrder =
                orderRepository.save(order);

        return toResponse(savedOrder);
    }

    /*
     * Frontend OrderSummary ile aynı hesap:
     *
     * productsTotal < 150  -> +29.99 shipping
     * productsTotal >= 150 -> free shipping
     */
    private BigDecimal calculateGrandTotal(
            BigDecimal productsTotal
    ) {

        BigDecimal grandTotal = productsTotal;

        if (productsTotal.compareTo(FREE_SHIPPING_LIMIT) < 0) {
            grandTotal =
                    grandTotal.add(SHIPPING_PAYMENT);
        }

        return grandTotal.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    /*
     * Kartın son kullanma ayı geçmişse siparişi reddeder.
     * Örneğin 09/2026 kartı Eylül 2026 boyunca geçerlidir.
     */
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

    /*
     * Sipariş kaydında tam kart numarası yerine
     * yalnızca son 4 haneyi tutuyoruz.
     */
    private String getLastFourDigits(
            String cardNo
    ) {

        String normalizedCardNo =
                cardNo.replaceAll("\\s+", "");

        if (!normalizedCardNo.matches("\\d{16}")) {
            throw new ApiException(
                    "Invalid card number.",
                    HttpStatus.BAD_REQUEST
            );
        }

        return normalizedCardNo.substring(
                normalizedCardNo.length() - 4
        );
    }

    /*
     * Entity -> Response DTO dönüşümü.
     */
    private OrderResponse toResponse(Order order) {

        List<OrderItemResponse> products =
                order.getProducts()
                        .stream()
                        .map(item -> {

                            Product product = item.getProduct();

                            String image = product.getImages().isEmpty()
                                    ? null
                                    : product.getImages().get(0).getUrl();

                            return new OrderItemResponse(
                                    product.getId(),
                                    product.getName(),
                                    product.getDescription(),
                                    image,
                                    item.getCount(),
                                    item.getDetail(),
                                    item.getUnitPrice()
                            );
                        })
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