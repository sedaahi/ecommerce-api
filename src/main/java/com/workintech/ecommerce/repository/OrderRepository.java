package com.workintech.ecommerce.repository;

import com.workintech.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     *Giriş yapan kullanıcı
     *         ↓
     * Sadece onun siparişleri
     *         ↓
     * En yeni sipariş en üstte
     */

    List<Order> findAllByUserEmailOrderByOrderDateDesc(String email);
}