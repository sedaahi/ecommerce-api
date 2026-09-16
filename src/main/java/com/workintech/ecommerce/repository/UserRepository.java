package com.workintech.ecommerce.repository;

import com.workintech.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Login sırasında kullanıcıyı email ile bulmak için.
    Optional<User> findByEmail(String email);

    // Signup sırasında aynı email daha önce kullanılmış mı kontrol etmek için.
    boolean existsByEmail(String email);
}