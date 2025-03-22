package com.fittlens.core.service;

import com.fittlens.core.model.Equipment;
import org.springframework.web.multipart.MultipartFile;

public interface ImageProcessingService {
    Equipment recognizeEquipment(MultipartFile image);
} 