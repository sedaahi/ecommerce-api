package com.workintech.ecommerce.repository;

import com.workintech.ecommerce.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Role'ü code değerine göre bulmak için.
    Optional<Role> findByCode(String code);
}