package com.fittlens.core.service;

import com.fittlens.core.dto.CreateEquipmentRequest;
import com.fittlens.core.dto.EquipmentRequest;
import com.fittlens.core.dto.EquipmentResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import com.fittlens.core.dto.ai.EquipmentRecognitionResponse;
public interface EquipmentService {
    EquipmentResponse addEquipment(EquipmentRequest request, String userId);
    EquipmentResponse createEquipment(CreateEquipmentRequest request, String userId);
    List<EquipmentResponse> listEquipment(String userId, String gymId);
    void deleteEquipment(String equipmentId, String userId);
    EquipmentRecognitionResponse analyzeEquipment(String imageUrl);
} 