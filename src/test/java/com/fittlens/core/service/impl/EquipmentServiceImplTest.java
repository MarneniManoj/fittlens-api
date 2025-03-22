package com.fittlens.core.service.impl;

import com.fittlens.core.dto.CreateEquipmentRequest;
import com.fittlens.core.dto.EquipmentRequest;
import com.fittlens.core.dto.EquipmentResponse;
import com.fittlens.core.model.Equipment;
import com.fittlens.core.model.User;
import com.fittlens.core.repository.EquipmentRepository;
import com.fittlens.core.repository.UserRepository;
import com.fittlens.core.service.ImageProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EquipmentServiceImplTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageProcessingService imageProcessingService;

    @InjectMocks
    private EquipmentServiceImpl equipmentService;

    private static final String USER_ID = "test-user-id";
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUuid(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
    }

    @Test
    void createEquipment_Success() {
        // Arrange
        CreateEquipmentRequest request = new CreateEquipmentRequest();
        request.setName("Test Equipment");
        request.setImageIcon("test-icon.jpg");
        request.setGymId("test-gym");

        Equipment savedEquipment = new Equipment();
        savedEquipment.setId("test-id");
        savedEquipment.setName(request.getName());
        savedEquipment.setImageIcon(request.getImageIcon());
        savedEquipment.setUser(testUser);

        when(equipmentRepository.save(any(Equipment.class))).thenReturn(savedEquipment);

        // Act
        EquipmentResponse response = equipmentService.createEquipment(request, USER_ID);

        // Assert
        assertNotNull(response);
        assertEquals(savedEquipment.getId(), response.getId());
        assertEquals(savedEquipment.getName(), response.getName());
        assertEquals(savedEquipment.getImageIcon(), response.getImageIcon());
        assertEquals(USER_ID, response.getUserId());
        verify(equipmentRepository).save(any(Equipment.class));
    }

    @Test
    void addEquipment_Success() {
        // Arrange
        EquipmentRequest request = new EquipmentRequest();
        request.setGymId("test-gym");
        request.setUserEquipmentImage(new MockMultipartFile(
            "test-image", "test.jpg", "image/jpeg", "test".getBytes()));

        Equipment recognizedEquipment = new Equipment();
        recognizedEquipment.setName("Recognized Equipment");
        when(imageProcessingService.recognizeEquipment(any())).thenReturn(recognizedEquipment);

        Equipment savedEquipment = new Equipment();
        savedEquipment.setId("test-id");
        savedEquipment.setName(recognizedEquipment.getName());
        savedEquipment.setUser(testUser);
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(savedEquipment);

        // Act
        EquipmentResponse response = equipmentService.addEquipment(request, USER_ID);

        // Assert
        assertNotNull(response);
        assertEquals(savedEquipment.getId(), response.getId());
        assertEquals(savedEquipment.getName(), response.getName());
        assertEquals(USER_ID, response.getUserId());
        verify(imageProcessingService).recognizeEquipment(request.getUserEquipmentImage());
        verify(equipmentRepository).save(any(Equipment.class));
    }

    @Test
    void listEquipment_WithGymId_Success() {
        // Arrange
        String gymId = "test-gym";
        List<Equipment> equipmentList = Arrays.asList(
            createTestEquipment("1", testUser),
            createTestEquipment("2", testUser)
        );

        when(equipmentRepository.findByUserUuidAndGymId(USER_ID, gymId))
            .thenReturn(equipmentList);

        // Act
        List<EquipmentResponse> response = equipmentService.listEquipment(USER_ID, gymId);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        verify(equipmentRepository).findByUserUuidAndGymId(USER_ID, gymId);
    }

    @Test
    void deleteEquipment_Success() {
        // Arrange
        String equipmentId = "test-equipment-id";
        Equipment equipment = createTestEquipment(equipmentId, testUser);
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));

        // Act
        equipmentService.deleteEquipment(equipmentId, USER_ID);

        // Assert
        verify(equipmentRepository).delete(equipment);
    }

    @Test
    void deleteEquipment_UnauthorizedUser_ThrowsException() {
        // Arrange
        String equipmentId = "test-equipment-id";
        User otherUser = new User();
        otherUser.setUuid("other-user-id");
        Equipment equipment = createTestEquipment(equipmentId, otherUser);
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            equipmentService.deleteEquipment(equipmentId, USER_ID));
    }

    private Equipment createTestEquipment(String id, User user) {
        Equipment equipment = new Equipment();
        equipment.setId(id);
        equipment.setName("Test Equipment " + id);
        equipment.setUser(user);
        return equipment;
    }
} 