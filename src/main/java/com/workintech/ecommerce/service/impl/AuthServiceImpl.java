package com.workintech.ecommerce.service.impl;

import com.workintech.ecommerce.dto.request.LoginRequest;
import com.workintech.ecommerce.dto.request.SignupRequest;
import com.workintech.ecommerce.dto.response.LoginResponse;
import com.workintech.ecommerce.entity.Role;
import com.workintech.ecommerce.entity.Store;
import com.workintech.ecommerce.entity.User;
import com.workintech.ecommerce.exception.ApiException;
import com.workintech.ecommerce.repository.RoleRepository;
import com.workintech.ecommerce.repository.StoreRepository;
import com.workintech.ecommerce.repository.UserRepository;
import com.workintech.ecommerce.security.JwtService;
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
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            StoreRepository storeRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.storeRepository = storeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public User signup(SignupRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        // Aynı email ile tekrar kayıt olunamaz.
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(
                    "Email already exists.",
                    HttpStatus.CONFLICT
            );
        }

        // Gönderilen role_id geçerli mi?
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ApiException(
                        "Role not found.",
                        HttpStatus.NOT_FOUND
                ));

        // Public signup üzerinden admin oluşturulamaz.
        if ("admin".equals(role.getCode())) {
            throw new ApiException(
                    "Admin role cannot be selected during signup.",
                    HttpStatus.FORBIDDEN
            );
        }

        // Store rolünde mağaza bilgileri zorunlu.
        if ("store".equals(role.getCode()) && request.getStore() == null) {
            throw new ApiException(
                    "Store information is required.",
                    HttpStatus.BAD_REQUEST
            );
        }

        // Store varsa tax number benzersiz olmalı.
        if ("store".equals(role.getCode())
                && storeRepository.existsByTaxNo(request.getStore().getTaxNo())) {

            throw new ApiException(
                    "Tax number already exists.",
                    HttpStatus.CONFLICT
            );
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);

        // Şifreyi BCrypt ile hashleyerek kaydediyoruz.
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(role);

        User savedUser = userRepository.save(user);

        // Store kullanıcısıysa mağaza kaydını da oluştur.
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

    @Override
    public LoginResponse login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        // Kullanıcı email ile bulunur.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(
                        "Invalid email or password.",
                        HttpStatus.UNAUTHORIZED
                ));

        // Girilen şifre BCrypt hash ile karşılaştırılır.
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new ApiException(
                    "Invalid email or password.",
                    HttpStatus.UNAUTHORIZED
            );
        }

        // Kullanıcı doğrulandıktan sonra JWT oluşturulur.
        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getId(),
                user.getRole().getName()
        );
    }
}