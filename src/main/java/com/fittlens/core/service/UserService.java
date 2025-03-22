package com.fittlens.core.service;

import com.fittlens.core.dto.UserRegistrationRequest;
import com.fittlens.core.dto.UserResponse;

public interface UserService {
    UserResponse registerUser(UserRegistrationRequest request);
    UserResponse getUserByUsername(String username);
} 