package com.fittlens.core.controller;

import com.fittlens.core.dto.UserRegistrationRequest;
import com.fittlens.core.dto.UserResponse;
import com.fittlens.core.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_Success() {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("TestUser");
        request.setDeviceId("test-device");
        request.setEmail("test@example.com");

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setUuid("test-uuid");
        expectedResponse.setName(request.getName());
        expectedResponse.setDeviceId(request.getDeviceId());
        expectedResponse.setEmail(request.getEmail());
        expectedResponse.setToken("test-token");

        when(userService.registerUser(any(UserRegistrationRequest.class)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.registerUser(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(userService).registerUser(request);
    }

    @Test
    void getUser_Success() {
        // Arrange
        String username = "TestUser";
        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setUuid("test-uuid");
        expectedResponse.setName(username);

        when(userService.getUserByUsername(username)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.getUser(username);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(userService).getUserByUsername(username);
    }
} 