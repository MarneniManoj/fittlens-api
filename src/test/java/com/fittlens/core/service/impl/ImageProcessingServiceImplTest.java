package com.fittlens.core.service.impl;

import com.fittlens.core.model.Equipment;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class ImageProcessingServiceImplTest {

    private final ImageProcessingServiceImpl imageProcessingService = new ImageProcessingServiceImpl();

    @Test
    void recognizeEquipment_Success() {
        // Arrange
        MultipartFile imageFile = new MockMultipartFile(
            "test-image", "test.jpg", "image/jpeg", "test".getBytes());

        // Act
        Equipment result = imageProcessingService.recognizeEquipment(imageFile);

        // Assert
        assertNotNull(result);
        assertEquals("Equipment Name", result.getName());
        assertEquals("https://example.com/image.jpg", result.getImageIcon());
    }
} 