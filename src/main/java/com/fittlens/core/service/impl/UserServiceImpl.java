package com.fittlens.core.service.impl;

import com.fittlens.core.dto.UserRegistrationRequest;
import com.fittlens.core.dto.UserResponse;
import com.fittlens.core.dto.LoginRequest;
import com.fittlens.core.dto.LoginResponse;
import com.fittlens.core.model.User;
import com.fittlens.core.repository.UserRepository;
import com.fittlens.core.service.UserService;
import com.fittlens.core.util.AnimalNameGenerator;
import com.fittlens.core.util.TokenGenerator;
import com.fittlens.core.util.JwtUtil;
import com.fittlens.core.exception.ResourceNotFoundException;
import com.fittlens.core.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AnimalNameGenerator animalNameGenerator;
    private final TokenGenerator tokenGenerator;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, 
                         AnimalNameGenerator animalNameGenerator,
                         TokenGenerator tokenGenerator,
                         PasswordEncoder passwordEncoder,
                         JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.animalNameGenerator = animalNameGenerator;
        this.tokenGenerator = tokenGenerator;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {
        User user = new User();
        user.setDeviceId(request.getDeviceId());
        user.setEmail(request.getEmail());
        
        // Hash password if provided
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        // Generate username if not provided
        String username = request.getName();
        if (username == null || username.trim().isEmpty()) {
            do {
                username = animalNameGenerator.generateName();
            } while (userRepository.existsByName(username));
        }
        user.setName(username);
        
        // Generate token
        user.setToken(tokenGenerator.generateToken());
        
        User savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByName(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return convertToUserResponse(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }

        String token = jwtUtil.generateToken(user);
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(convertToUserResponse(user));
        return response;
    }

    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUuid(user.getUuid());
        response.setName(user.getName());
        response.setDeviceId(user.getDeviceId());
        response.setEmail(user.getEmail());
        response.setToken(user.getToken());
        return response;
    }
}