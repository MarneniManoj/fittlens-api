package com.fittlens.core.controller;

import com.fittlens.core.dto.CreateEquipmentRequest;
import com.fittlens.core.dto.EquipmentRequest;
import com.fittlens.core.dto.EquipmentResponse;
import com.fittlens.core.service.EquipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EquipmentControllerTest {

    @Mock
    private EquipmentService equipmentService;

    @InjectMocks
    private EquipmentController equipmentController;

    private static final String USER_ID = "test-user-id";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEquipment_Success() {
        // Arrange
        CreateEquipmentRequest request = new CreateEquipmentRequest();
        request.setName("Test Equipment");
        request.setImageIcon("test-icon.jpg");
        request.setGymId("test-gym");

        EquipmentResponse expectedResponse = new EquipmentResponse();
        expectedResponse.setId("test-id");
        expectedResponse.setName(request.getName());
        expectedResponse.setImageIcon(request.getImageIcon());

        when(equipmentService.createEquipment(any(CreateEquipmentRequest.class), eq(USER_ID)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<EquipmentResponse> response = equipmentController.createEquipment(request, USER_ID);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(equipmentService).createEquipment(request, USER_ID);
    }

    @Test
    void addEquipment_Success() {
        // Arrange
        EquipmentRequest request = new EquipmentRequest();
        request.setGymId("test-gym");
        request.setUserEquipmentImage(new MockMultipartFile(
            "test-image", "test.jpg", "image/jpeg", "test".getBytes()));

        EquipmentResponse expectedResponse = new EquipmentResponse();
        expectedResponse.setId("test-id");
        expectedResponse.setName("Test Equipment");

        when(equipmentService.addEquipment(any(EquipmentRequest.class), eq(USER_ID)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<EquipmentResponse> response = equipmentController.addEquipment(request, USER_ID);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(equipmentService).addEquipment(request, USER_ID);
    }

    @Test
    void listEquipment_Success() {
        // Arrange
        String gymId = "test-gym";
        List<EquipmentResponse> expectedEquipment = Arrays.asList(
            new EquipmentResponse(), new EquipmentResponse()
        );

        when(equipmentService.listEquipment(USER_ID, gymId)).thenReturn(expectedEquipment);

        // Act
        ResponseEntity<List<EquipmentResponse>> response = equipmentController.listEquipment(USER_ID, gymId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedEquipment, response.getBody());
        verify(equipmentService).listEquipment(USER_ID, gymId);
    }

    @Test
    void deleteEquipment_Success() {
        // Arrange
        String equipmentId = "test-equipment-id";
        doNothing().when(equipmentService).deleteEquipment(equipmentId, USER_ID);

        // Act
        ResponseEntity<Void> response = equipmentController.deleteEquipment(equipmentId, USER_ID);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(equipmentService).deleteEquipment(equipmentId, USER_ID);
    }
} 