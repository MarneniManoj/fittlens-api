package com.fittlens.core.service;

import com.fittlens.core.dto.UserRegistrationRequest;
import com.fittlens.core.dto.UserResponse;
import com.fittlens.core.dto.LoginRequest;
import com.fittlens.core.dto.LoginResponse;

public interface UserService {
    UserResponse registerUser(UserRegistrationRequest request);
    UserResponse getUserByUsername(String username);
    LoginResponse login(LoginRequest request);
} 