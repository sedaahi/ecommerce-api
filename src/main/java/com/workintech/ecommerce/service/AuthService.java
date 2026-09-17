package com.workintech.ecommerce.service;

import com.workintech.ecommerce.dto.request.LoginRequest;
import com.workintech.ecommerce.dto.request.SignupRequest;
import com.workintech.ecommerce.dto.response.LoginResponse;
import com.workintech.ecommerce.entity.User;

public interface AuthService {

    User signup(SignupRequest request);

    LoginResponse login(LoginRequest request);
}