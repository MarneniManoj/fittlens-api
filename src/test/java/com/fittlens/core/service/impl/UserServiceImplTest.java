package com.fittlens.core.service.impl;

import com.fittlens.core.dto.UserRegistrationRequest;
import com.fittlens.core.dto.UserResponse;
import com.fittlens.core.model.User;
import com.fittlens.core.repository.UserRepository;
import com.fittlens.core.util.AnimalNameGenerator;
import com.fittlens.core.util.TokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AnimalNameGenerator animalNameGenerator;

    @Mock
    private TokenGenerator tokenGenerator;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_WithProvidedName_Success() {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("TestUser");
        request.setDeviceId("test-device");
        request.setEmail("test@example.com");

        User savedUser = new User();
        savedUser.setUuid("test-uuid");
        savedUser.setName(request.getName());
        savedUser.setDeviceId(request.getDeviceId());
        savedUser.setEmail(request.getEmail());
        savedUser.setToken("test-token");

        when(tokenGenerator.generateToken()).thenReturn("test-token");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponse response = userService.registerUser(request);

        // Assert
        assertNotNull(response);
        assertEquals(savedUser.getUuid(), response.getUuid());
        assertEquals(savedUser.getName(), response.getName());
        assertEquals(savedUser.getToken(), response.getToken());
        verify(userRepository).save(any(User.class));
        verify(tokenGenerator).generateToken();
        verify(animalNameGenerator, never()).generateName();
    }

    @Test
    void registerUser_WithGeneratedName_Success() {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setDeviceId("test-device");
        request.setEmail("test@example.com");

        String generatedName = "Lion123";
        when(animalNameGenerator.generateName()).thenReturn(generatedName);
        when(userRepository.existsByName(generatedName)).thenReturn(false);
        when(tokenGenerator.generateToken()).thenReturn("test-token");

        User savedUser = new User();
        savedUser.setUuid("test-uuid");
        savedUser.setName(generatedName);
        savedUser.setDeviceId(request.getDeviceId());
        savedUser.setToken("test-token");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponse response = userService.registerUser(request);

        // Assert
        assertNotNull(response);
        assertEquals(generatedName, response.getName());
        verify(animalNameGenerator).generateName();
        verify(userRepository).existsByName(generatedName);
    }

    @Test
    void getUserByUsername_Success() {
        // Arrange
        String username = "TestUser";
        User user = new User();
        user.setUuid("test-uuid");
        user.setName(username);
        user.setToken("test-token");

        when(userRepository.findByName(username)).thenReturn(Optional.of(user));

        // Act
        UserResponse response = userService.getUserByUsername(username);

        // Assert
        assertNotNull(response);
        assertEquals(user.getUuid(), response.getUuid());
        assertEquals(user.getName(), response.getName());
        verify(userRepository).findByName(username);
    }

    @Test
    void getUserByUsername_UserNotFound_ThrowsException() {
        // Arrange
        String username = "NonExistentUser";
        when(userRepository.findByName(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            userService.getUserByUsername(username));
        verify(userRepository).findByName(username);
    }
} 