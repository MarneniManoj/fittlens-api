package com.fittlens.core.service.impl;

import com.fittlens.core.dto.UserRegistrationRequest;
import com.fittlens.core.dto.UserResponse;
import com.fittlens.core.model.User;
import com.fittlens.core.repository.UserRepository;
import com.fittlens.core.service.UserService;
import com.fittlens.core.util.AnimalNameGenerator;
import com.fittlens.core.util.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AnimalNameGenerator animalNameGenerator;
    private final TokenGenerator tokenGenerator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, 
                         AnimalNameGenerator animalNameGenerator,
                         TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.animalNameGenerator = animalNameGenerator;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {
        User user = new User();
        user.setDeviceId(request.getDeviceId());
        user.setEmail(request.getEmail());
        
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
            .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToUserResponse(user);
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