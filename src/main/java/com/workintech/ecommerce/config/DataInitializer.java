package com.workintech.ecommerce.config;

import com.workintech.ecommerce.entity.Role;
import com.workintech.ecommerce.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {

            // Uygulama ilk kez çalıştığında gerekli sistem rollerini oluşturur.
            if (roleRepository.findByCode("admin").isEmpty()) {
                Role admin = new Role();
                admin.setCode("admin");
                admin.setName("Admin");
                roleRepository.save(admin);
            }

            if (roleRepository.findByCode("store").isEmpty()) {
                Role store = new Role();
                store.setCode("store");
                store.setName("Store");
                roleRepository.save(store);
            }

            if (roleRepository.findByCode("customer").isEmpty()) {
                Role customer = new Role();
                customer.setCode("customer");
                customer.setName("Customer");
                roleRepository.save(customer);
            }
        };
    }
}