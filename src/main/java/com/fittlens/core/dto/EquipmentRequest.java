package com.fittlens.core.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EquipmentRequest {
    private String gymId;
    private MultipartFile userEquipmentImage;
} 