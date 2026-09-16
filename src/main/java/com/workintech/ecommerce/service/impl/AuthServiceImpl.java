package com.workintech.ecommerce.service.impl;

import com.workintech.ecommerce.dto.request.SignupRequest;
import com.workintech.ecommerce.entity.Role;
import com.workintech.ecommerce.entity.Store;
import com.workintech.ecommerce.entity.User;
import com.workintech.ecommerce.exception.ApiException;
import com.workintech.ecommerce.repository.RoleRepository;
import com.workintech.ecommerce.repository.StoreRepository;
import com.workintech.ecommerce.repository.UserRepository;
import com.workintech.ecommerce.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            StoreRepository storeRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.storeRepository = storeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User signup(SignupRequest request) {

        // Email'i standart hale getiriyoruz.
        String email = request.getEmail().trim().toLowerCase();

        // Aynı email ile ikinci kez kayıt olunamaz.
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(
                    "Email already exists.",
                    HttpStatus.CONFLICT
            );
        }

        // Frontend'den gelen role_id gerçekten var mı?
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ApiException(
                        "Role not found.",
                        HttpStatus.NOT_FOUND
                ));

        // Kullanıcı signup üzerinden kendisini admin yapamaz.
        if ("admin".equals(role.getCode())) {
            throw new ApiException(
                    "Admin role cannot be selected during signup.",
                    HttpStatus.FORBIDDEN
            );
        }

        // Store rolü seçilmişse store bilgileri zorunlu.
        if ("store".equals(role.getCode()) && request.getStore() == null) {
            throw new ApiException(
                    "Store information is required.",
                    HttpStatus.BAD_REQUEST
            );
        }

        /*
         * Store oluşturulacaksa tax number kontrolünü
         * User kaydından önce yapıyoruz.
         */
        if ("store".equals(role.getCode())
                && storeRepository.existsByTaxNo(request.getStore().getTaxNo())) {

            throw new ApiException(
                    "Tax number already exists.",
                    HttpStatus.CONFLICT
            );
        }

        // User entity oluşturulur.
        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);

        // Şifre DB'ye düz metin olarak değil BCrypt hash olarak kaydedilir.
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(role);

        User savedUser = userRepository.save(user);

        // Role Store ise stores tablosuna da kayıt oluşturulur.
        if ("store".equals(role.getCode())) {

            Store store = new Store();

            store.setName(request.getStore().getName().trim());
            store.setPhone(request.getStore().getPhone());
            store.setTaxNo(request.getStore().getTaxNo());
            store.setBankAccount(request.getStore().getBankAccount());
            store.setUser(savedUser);

            storeRepository.save(store);
        }

        return savedUser;
    }
}