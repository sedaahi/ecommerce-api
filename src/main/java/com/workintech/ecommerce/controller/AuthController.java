package com.workintech.ecommerce.controller;

import com.workintech.ecommerce.dto.request.LoginRequest;
import com.workintech.ecommerce.dto.request.SignupRequest;
import com.workintech.ecommerce.dto.response.LoginResponse;
import com.workintech.ecommerce.dto.response.UserResponse;
import com.workintech.ecommerce.entity.User;
import com.workintech.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(
            @Valid @RequestBody SignupRequest request
    ) {

        User user = authService.signup(request);

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getId(),
                user.getRole().getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<UserResponse> verify(
            Authentication authentication
    ) {

        UserResponse response =
                authService.verify(authentication.getName());

        return ResponseEntity.ok(response);
    }
}