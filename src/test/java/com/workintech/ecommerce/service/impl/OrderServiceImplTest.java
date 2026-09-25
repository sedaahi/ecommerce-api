package com.workintech.ecommerce.service.impl;

import com.workintech.ecommerce.dto.request.OrderItemRequest;
import com.workintech.ecommerce.dto.request.OrderRequest;
import com.workintech.ecommerce.dto.response.OrderResponse;
import com.workintech.ecommerce.entity.Address;
import com.workintech.ecommerce.entity.Order;
import com.workintech.ecommerce.entity.Product;
import com.workintech.ecommerce.entity.User;
import com.workintech.ecommerce.repository.AddressRepository;
import com.workintech.ecommerce.repository.OrderRepository;
import com.workintech.ecommerce.repository.ProductRepository;
import com.workintech.ecommerce.repository.UserRepository;
import com.workintech.ecommerce.exception.ApiException;
import com.workintech.ecommerce.entity.OrderItem;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @Mock // Sahte dependency oluştur
 * @InjectMocks // Bu sahteleri test edeceğimiz gerçek service'e ver
 *
 * when(...)   // Mock çağrıldığında ne döneceğini belirle
 * verify(...) // Gerçekten çağrılmış mı kontrol et
 */


// JUnit 5 testlerinde Mockito kullanımını aktif eder.
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    // Gerçek veritabanına gitmemek için repository'lerin mock'ları oluşturulur.
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ProductRepository productRepository;

    // Yukarıdaki mock repository'leri gerçek OrderServiceImpl içine enjekte eder.
    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Geçerli bilgilerle sipariş başarıyla oluşturulmalı")
    void createOrder_shouldCreateOrderSuccessfully() {

        // ARRANGE: Test için gerekli kullanıcı, adres, ürün ve request hazırlanır.
        String email = "test@example.com";

        User user = new User();

        Address address = new Address();
        address.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setName("Test Product");
        product.setDescription("Test product description");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(5);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(10L);
        itemRequest.setCount(1);
        itemRequest.setDetail("Test Product");

        OrderRequest request = new OrderRequest();
        request.setAddressId(1L);
        request.setOrderDate(LocalDateTime.now());
        request.setCardNo("1234567890123456");
        request.setCardName("Seda Ahi");
        request.setCardExpireMonth(12);
        request.setCardExpireYear(2035);
        request.setCardCcv("123");

        // Ürün 100 TL ve 150 TL altı olduğu için 29.99 TL kargo eklenir.
        request.setPrice(new BigDecimal("129.99"));

        request.setProducts(List.of(itemRequest));

        // Mock repository'lerin hangi verileri döndüreceği belirlenir.
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(addressRepository.findByIdAndUserEmail(1L, email))
                .thenReturn(Optional.of(address));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        // save() çağrısında gelen Order'a DB tarafından verilmiş gibi id atanır.
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(100L);
                    return order;
                });

        // ACT: Test edilen gerçek service metodu çalıştırılır.
        OrderResponse response =
                orderService.createOrder(email, request);

        // ASSERT: Oluşturulan siparişin beklediğimiz değerlere sahip olduğu kontrol edilir.
        assertEquals(100L, response.getId());
        assertEquals(
                new BigDecimal("129.99"),
                response.getPrice()
        );

        // Tam kart numarası yerine yalnızca son 4 hanenin tutulduğunu doğrular.
        assertEquals("3456", response.getCardLastFour());

        // Başlangıç stoğu 5, satın alınan miktar 1 → kalan stok 4 olmalı.
        assertEquals(4, product.getStock());

        // Service'in gerekli repository metotlarını çağırdığını doğrular.
        verify(userRepository).findByEmail(email);

        verify(addressRepository)
                .findByIdAndUserEmail(1L, email);

        verify(productRepository).findById(10L);
        verify(productRepository).save(product);

        // Siparişin en sonunda kaydedildiğini doğrular.
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Ürün stoğu yetersizse sipariş oluşturulmamalı")
    void createOrder_shouldThrowExceptionWhenStockIsInsufficient() {

        // ARRANGE: Ürünün stoğu 2, kullanıcı ise 5 adet almaya çalışıyor.
        String email = "test@example.com";

        User user = new User();

        Address address = new Address();
        address.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(2);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(10L);
        itemRequest.setCount(5);
        itemRequest.setDetail("Test Product");

        OrderRequest request = new OrderRequest();
        request.setAddressId(1L);
        request.setOrderDate(LocalDateTime.now());
        request.setCardNo("1234567890123456");
        request.setCardName("Seda Ahi");
        request.setCardExpireMonth(12);
        request.setCardExpireYear(2035);
        request.setCardCcv("123");
        request.setPrice(new BigDecimal("500.00"));
        request.setProducts(List.of(itemRequest));

        // Mock repository davranışları hazırlanır.
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(addressRepository.findByIdAndUserEmail(1L, email))
                .thenReturn(Optional.of(address));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        // ACT + ASSERT:
        // Stok yetersiz olduğu için ApiException fırlatılmasını bekliyoruz.
        ApiException exception = assertThrows(
                ApiException.class,
                () -> orderService.createOrder(email, request)
        );

        // Doğru hata mesajının üretildiğini kontrol eder.
        assertEquals(
                "Insufficient stock for product: Test Product",
                exception.getMessage()
        );

        // Sipariş başarısız olduğu için Order DB'ye kaydedilmemeli.
        verify(orderRepository, never())
                .save(any(Order.class));

        // Stok da değiştirilip kaydedilmemeli.
        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    @DisplayName("Frontend fiyatı backend hesabıyla uyuşmuyorsa sipariş oluşturulmamalı")
    void createOrder_shouldThrowExceptionWhenPriceDoesNotMatch() {

        // ARRANGE: Backend'e göre gerçek toplam 100 + 29.99 kargo = 129.99 TL.
        String email = "test@example.com";

        User user = new User();

        Address address = new Address();
        address.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setName("Test Product");
        product.setDescription("Test product description");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(5);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(10L);
        itemRequest.setCount(1);
        itemRequest.setDetail("Test Product");

        OrderRequest request = new OrderRequest();
        request.setAddressId(1L);
        request.setOrderDate(LocalDateTime.now());
        request.setCardNo("1234567890123456");
        request.setCardName("Seda Ahi");
        request.setCardExpireMonth(12);
        request.setCardExpireYear(2035);
        request.setCardCcv("123");

        // Frontend gerçek toplam olan 129.99 yerine yanlış fiyat gönderiyor.
        request.setPrice(new BigDecimal("50.00"));

        request.setProducts(List.of(itemRequest));

        // Repository'lerin test sırasında döndüreceği veriler hazırlanır.
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(addressRepository.findByIdAndUserEmail(1L, email))
                .thenReturn(Optional.of(address));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        // ACT + ASSERT:
        // Frontend fiyatı ile backend hesabı uyuşmadığı için hata beklenir.
        ApiException exception = assertThrows(
                ApiException.class,
                () -> orderService.createOrder(email, request)
        );

        // Beklediğimiz business validation hatasının geldiğini doğrular.
        assertEquals(
                "Order price does not match calculated price.",
                exception.getMessage()
        );

        // Fiyat doğrulanamadığı için sipariş kaydedilmemeli.
        verify(orderRepository, never())
                .save(any(Order.class));

        // Sipariş başarısız olduğu için ürün stoğu da DB'de güncellenmemeli.
        verify(productRepository, never())
                .save(any(Product.class));

        // Ürün nesnesinin stoğu da değişmemiş olmalı.
        assertEquals(5, product.getStock());
    }

    @Test
    @DisplayName("Kartın son kullanma tarihi geçmişse sipariş oluşturulmamalı")
    void createOrder_shouldThrowExceptionWhenCardIsExpired() {

        // ARRANGE: Geçmiş tarihli bir kart hazırlanır.
        String email = "test@example.com";

        User user = new User();

        Address address = new Address();
        address.setId(1L);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(10L);
        itemRequest.setCount(1);
        itemRequest.setDetail("Test Product");

        OrderRequest request = new OrderRequest();
        request.setAddressId(1L);
        request.setOrderDate(LocalDateTime.now());
        request.setCardNo("1234567890123456");
        request.setCardName("Seda Ahi");

        // Test her yıl çalışabilsin diye sabit bir geçmiş yıl kullanmıyoruz.
        request.setCardExpireMonth(YearMonth.now().minusMonths(1).getMonthValue());
        request.setCardExpireYear(YearMonth.now().minusMonths(1).getYear());

        request.setCardCcv("123");
        request.setPrice(new BigDecimal("129.99"));
        request.setProducts(List.of(itemRequest));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(addressRepository.findByIdAndUserEmail(1L, email))
                .thenReturn(Optional.of(address));

        // ACT + ASSERT:
        // Kartın son kullanma tarihi geçtiği için hata beklenir.
        ApiException exception = assertThrows(
                ApiException.class,
                () -> orderService.createOrder(email, request)
        );

        assertEquals(
                "Card has expired.",
                exception.getMessage()
        );

        // Kart kontrolünde hata oluştuğu için ürün sorgusuna bile geçilmemeli.
        verify(productRepository, never())
                .findById(any());

        // Başarısız sipariş kesinlikle kaydedilmemeli.
        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    @DisplayName("Kullanıcının geçmiş siparişleri doğru şekilde getirilmeli")
    void getOrders_shouldReturnUserOrders() {

        // ARRANGE: Kullanıcıya ait örnek bir sipariş hazırlanır.
        String email = "test@example.com";

        Address address = new Address();
        address.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setName("Test Product");
        product.setDescription("Test product description");
        product.setPrice(new BigDecimal("100.00"));

        // Response oluşturulurken ilk ürün görseline bakıldığı için boş liste verilir.
        product.setImages(List.of());

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setCount(1);
        orderItem.setDetail("Test Product");
        orderItem.setUnitPrice(new BigDecimal("100.00"));

        Order order = new Order();
        order.setId(100L);
        order.setOrderDate(LocalDateTime.now());
        order.setPrice(new BigDecimal("129.99"));
        order.setAddress(address);
        order.setCardName("Seda Ahi");
        order.setCardLastFour("3456");
        order.setCardExpireMonth(12);
        order.setCardExpireYear(2035);
        order.setProducts(List.of(orderItem));

        // Repository çağrıldığında hazırladığımız siparişi döndürür.
        when(orderRepository.findAllByUserEmailOrderByOrderDateDesc(email))
                .thenReturn(List.of(order));

        // ACT: Kullanıcının siparişleri getirilir.
        List<OrderResponse> responses =
                orderService.getOrders(email);

        // ASSERT: Bir sipariş döndüğünü ve temel bilgilerin doğru maplendiğini kontrol eder.
        assertEquals(1, responses.size());

        OrderResponse response = responses.get(0);

        assertEquals(100L, response.getId());
        assertEquals(new BigDecimal("129.99"), response.getPrice());
        assertEquals("3456", response.getCardLastFour());
        assertEquals(1, response.getProducts().size());

        // Repository'nin doğru kullanıcı email'i ile çağrıldığını doğrular.
        verify(orderRepository)
                .findAllByUserEmailOrderByOrderDateDesc(email);
    }

    @Test
    @DisplayName("Kullanıcıya ait olmayan adresle sipariş oluşturulmamalı")
    void createOrder_shouldThrowExceptionWhenAddressDoesNotBelongToUser() {

        // ARRANGE: Kullanıcı mevcut ancak verilen adres bu kullanıcıya ait değil.
        String email = "test@example.com";

        User user = new User();

        OrderRequest request = new OrderRequest();
        request.setAddressId(99L);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        // Kullanıcı + adres eşleşmesi bulunamadığını simüle eder.
        when(addressRepository.findByIdAndUserEmail(99L, email))
                .thenReturn(Optional.empty());

        // ACT + ASSERT: Adres bulunamadığı için sipariş oluşturulamamalı.
        ApiException exception = assertThrows(
                ApiException.class,
                () -> orderService.createOrder(email, request)
        );

        assertEquals(
                "Address not found.",
                exception.getMessage()
        );

        // Adres kontrolünde işlem durduğu için ürünlere geçilmemeli.
        verify(productRepository, never())
                .findById(any());

        // Sipariş kesinlikle kaydedilmemeli.
        verify(orderRepository, never())
                .save(any(Order.class));
    }
}