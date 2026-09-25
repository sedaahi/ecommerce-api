package com.workintech.ecommerce.service.impl;

import com.workintech.ecommerce.dto.request.LoginRequest;
import com.workintech.ecommerce.dto.request.SignupRequest;
import com.workintech.ecommerce.dto.response.LoginResponse;
import com.workintech.ecommerce.entity.Role;
import com.workintech.ecommerce.entity.User;
import com.workintech.ecommerce.exception.ApiException;
import com.workintech.ecommerce.repository.RoleRepository;
import com.workintech.ecommerce.repository.StoreRepository;
import com.workintech.ecommerce.repository.UserRepository;
import com.workintech.ecommerce.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// JUnit 5 testlerinde Mockito kullanımını aktif eder.
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    // Gerçek DB, BCrypt ve JWT servisi yerine mock nesneler kullanılır.
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    // Mock bağımlılıkları gerçek AuthServiceImpl içine enjekte eder.
    @InjectMocks
    private AuthServiceImpl authService;


    @Test
    @DisplayName("Geçerli bilgilerle customer kullanıcısı oluşturulmalı")
    void signup_shouldCreateCustomerSuccessfully() {

        // ARRANGE
        Role customerRole = new Role();
        customerRole.setId(3L);
        customerRole.setCode("customer");
        customerRole.setName("Customer");

        SignupRequest request = new SignupRequest();
        request.setName("Seda Ahi");
        request.setEmail("SEDA@EXAMPLE.COM");
        request.setPassword("Password123!");
        request.setRoleId(3L);

        // Email'in daha önce kullanılmadığını simüle eder.
        when(userRepository.existsByEmail("seda@example.com"))
                .thenReturn(false);

        when(roleRepository.findById(3L))
                .thenReturn(Optional.of(customerRole));

        // Gerçek BCrypt çalıştırmak yerine örnek hash döndürür.
        when(passwordEncoder.encode("Password123!"))
                .thenReturn("hashed-password");

        // save() metoduna gelen User nesnesini geri döndürür.
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        // ACT
        User result = authService.signup(request);

        // ASSERT
        assertEquals(1L, result.getId());
        assertEquals("Seda Ahi", result.getName());
        assertEquals("seda@example.com", result.getEmail());
        assertEquals("hashed-password", result.getPassword());
        assertEquals(customerRole, result.getRole());

        verify(passwordEncoder).encode("Password123!");
        verify(userRepository).save(any(User.class));

        // Customer signup olduğu için Store kaydı oluşturulmamalı.
        verify(storeRepository, never()).save(any());
    }


    @Test
    @DisplayName("Kayıtlı email ile tekrar signup yapılamamalı")
    void signup_shouldThrowExceptionWhenEmailAlreadyExists() {

        // ARRANGE
        SignupRequest request = new SignupRequest();
        request.setName("Seda Ahi");
        request.setEmail("SEDA@EXAMPLE.COM");
        request.setPassword("Password123!");
        request.setRoleId(3L);

        when(userRepository.existsByEmail("seda@example.com"))
                .thenReturn(true);

        // ACT + ASSERT
        ApiException exception = assertThrows(
                ApiException.class,
                () -> authService.signup(request)
        );

        assertEquals(
                "Email already exists.",
                exception.getMessage()
        );

        // Email zaten kayıtlıysa kullanıcı oluşturma aşamasına geçilmemeli.
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }


    @Test
    @DisplayName("Doğru email ve şifre ile login başarılı olmalı ve JWT üretilmeli")
    void login_shouldLoginSuccessfully() {

        // ARRANGE
        Role customerRole = new Role();
        customerRole.setId(3L);
        customerRole.setCode("customer");
        customerRole.setName("Customer");

        User user = new User();
        user.setId(1L);
        user.setName("Seda Ahi");
        user.setEmail("seda@example.com");
        user.setPassword("hashed-password");
        user.setRole(customerRole);

        LoginRequest request = new LoginRequest();
        request.setEmail("SEDA@EXAMPLE.COM");
        request.setPassword("Password123!");

        when(userRepository.findByEmail("seda@example.com"))
                .thenReturn(Optional.of(user));

        // Girilen şifrenin DB'deki hash ile eşleştiğini simüle eder.
        when(passwordEncoder.matches(
                "Password123!",
                "hashed-password"
        )).thenReturn(true);

        when(jwtService.generateToken("seda@example.com"))
                .thenReturn("test-jwt-token");

        // ACT
        LoginResponse response = authService.login(request);

        // ASSERT
        assertEquals("test-jwt-token", response.getToken());
        assertEquals(1L, response.getId());
        assertEquals("seda@example.com", response.getEmail());

        // Başarılı login sonucunda JWT üretildiğini doğrular.
        verify(jwtService)
                .generateToken("seda@example.com");
    }


    @Test
    @DisplayName("Yanlış şifre ile login yapılamamalı ve JWT üretilmemeli")
    void login_shouldThrowExceptionWhenPasswordIsWrong() {

        // ARRANGE
        User user = new User();
        user.setEmail("seda@example.com");
        user.setPassword("hashed-password");

        LoginRequest request = new LoginRequest();
        request.setEmail("seda@example.com");
        request.setPassword("WrongPassword!");

        when(userRepository.findByEmail("seda@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "WrongPassword!",
                "hashed-password"
        )).thenReturn(false);

        // ACT + ASSERT
        ApiException exception = assertThrows(
                ApiException.class,
                () -> authService.login(request)
        );

        assertEquals(
                "Invalid email or password.",
                exception.getMessage()
        );

        // Kimlik doğrulama başarısızsa JWT kesinlikle üretilmemeli.
        verify(jwtService, never())
                .generateToken(any());
    }
}