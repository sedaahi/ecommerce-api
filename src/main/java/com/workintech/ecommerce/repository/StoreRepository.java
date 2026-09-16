package com.workintech.ecommerce.repository;

import com.workintech.ecommerce.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

    // Aynı vergi numarasıyla ikinci mağaza açılmasını engellemek için.
    boolean existsByTaxNo(String taxNo);
}