package com.fittlens.core.service.impl;

import com.fittlens.core.dto.CreateEquipmentRequest;
import com.fittlens.core.dto.EquipmentRequest;
import com.fittlens.core.dto.EquipmentResponse;
import com.fittlens.core.model.Equipment;
import com.fittlens.core.model.User;
import com.fittlens.core.repository.EquipmentRepository;
import com.fittlens.core.repository.UserRepository;
import com.fittlens.core.service.EquipmentService;
import com.fittlens.core.service.ImageProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final ImageProcessingService imageProcessingService;

    @Autowired
    public EquipmentServiceImpl(EquipmentRepository equipmentRepository,
                              UserRepository userRepository,
                              ImageProcessingService imageProcessingService) {
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.imageProcessingService = imageProcessingService;
    }

    @Override
    @Transactional
    public EquipmentResponse addEquipment(EquipmentRequest request, String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Process and recognize equipment from image
        Equipment recognizedEquipment = imageProcessingService.recognizeEquipment(request.getUserEquipmentImage());
        recognizedEquipment.setUser(user);
        recognizedEquipment.setGymId(request.getGymId());

        Equipment savedEquipment = equipmentRepository.save(recognizedEquipment);
        return convertToEquipmentResponse(savedEquipment);
    }

    @Override
    @Transactional
    public EquipmentResponse createEquipment(CreateEquipmentRequest request, String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Equipment equipment = new Equipment();
        equipment.setName(request.getName());
        equipment.setImageIcon(request.getImageIcon());
        equipment.setGymId(request.getGymId());
        equipment.setUser(user);

        Equipment savedEquipment = equipmentRepository.save(equipment);
        return convertToEquipmentResponse(savedEquipment);
    }

    @Override
    public List<EquipmentResponse> listEquipment(String userId, String gymId) {
        List<Equipment> equipment;
        if (gymId != null) {
            equipment = equipmentRepository.findByUserUuidAndGymId(userId, gymId);
        } else {
            equipment = equipmentRepository.findByUserUuid(userId);
        }
        
        return equipment.stream()
            .map(this::convertToEquipmentResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEquipment(String equipmentId, String userId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
            .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (!equipment.getUser().getUuid().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this equipment");
        }

        equipmentRepository.delete(equipment);
    }

    private EquipmentResponse convertToEquipmentResponse(Equipment equipment) {
        EquipmentResponse response = new EquipmentResponse();
        response.setId(equipment.getId());
        response.setName(equipment.getName());
        response.setImageIcon(equipment.getImageIcon());
        response.setUserId(equipment.getUser().getUuid());
        // Set possible exercises if needed
        return response;
    }
} 