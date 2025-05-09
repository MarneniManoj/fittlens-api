package com.fittlens.core.service.impl;

import com.fittlens.core.dto.CreateEquipmentRequest;
import com.fittlens.core.dto.EquipmentRequest;
import com.fittlens.core.dto.EquipmentResponse;
import com.fittlens.core.dto.ai.EquipmentRecognitionResponse;
import com.fittlens.core.exception.ResourceNotFoundException;
import com.fittlens.core.exception.UnauthorizedException;
import com.fittlens.core.model.Equipment;
import com.fittlens.core.model.Exercise;
import com.fittlens.core.model.User;
import com.fittlens.core.repository.EquipmentRepository;
import com.fittlens.core.repository.ExerciseRepository;
import com.fittlens.core.repository.UserRepository;
import com.fittlens.core.service.EquipmentService;
import com.fittlens.core.service.ImageProcessingService;
import com.fittlens.core.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final ImageProcessingService imageProcessingService;
    private final OpenAIService openAIService;

    @Autowired
    public EquipmentServiceImpl(EquipmentRepository equipmentRepository,
                                UserRepository userRepository,
                                ExerciseRepository exerciseRepository,
                                ImageProcessingService imageProcessingService,
                                OpenAIService openAIService) {
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.imageProcessingService = imageProcessingService;
        this.openAIService = openAIService;
    }

    @Override
    @Transactional
    public EquipmentResponse addEquipment(EquipmentRequest request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Process and recognize equipment from image
        Equipment recognizedEquipment = imageProcessingService.recognizeEquipment(request.getUserEquipmentImage());
        recognizedEquipment.setUsers(Set.of(user));
        recognizedEquipment.setGymId(request.getGymId());

        Equipment savedEquipment = equipmentRepository.save(recognizedEquipment);
        return convertToEquipmentResponse(savedEquipment);
    }

    @Override
    @Transactional
    public EquipmentResponse createEquipment(CreateEquipmentRequest request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Equipment equipment = new Equipment();
        equipment.setName(request.getName());
        equipment.setImageIcon(request.getImageIcon());
        equipment.setGymId(request.getGymId());
        equipment.setUsers(Set.of(user));

        Equipment savedEquipment = equipmentRepository.save(equipment);
        return convertToEquipmentResponse(savedEquipment);
    }

    @Override
    public List<EquipmentResponse> listEquipment(String userId, String gymId) {
        List<Equipment> equipment;

        equipment = equipmentRepository.findByUserId(userId);


        return equipment.stream()
                .map(this::convertToEquipmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEquipment(String equipmentId, String userId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + equipmentId));

        if (!equipment.getUsers().stream().anyMatch(user -> user.getUuid().equals(userId))) {
            throw new UnauthorizedException("Unauthorized to delete this equipment");
        }

        equipmentRepository.delete(equipment);
    }

    private EquipmentResponse convertToEquipmentResponse(Equipment equipment) {
        EquipmentResponse response = new EquipmentResponse();
        response.setId(equipment.getId());
        response.setName(equipment.getName());
        response.setImageIcon(equipment.getImageIcon());
        response.setUserId(equipment.getUsers().iterator().next().getUuid());
        // Set possible exercises if needed
        return response;
    }

    @Override
    @Transactional
    public EquipmentRecognitionResponse analyzeEquipment(String imageUrl) {

        User user = userRepository.findByEmail(getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found "));

        EquipmentRecognitionResponse er = openAIService.recognizeEquipmentFromImageUrl(imageUrl);

        // Create and save exercises first
        Set<Exercise> exercises = new HashSet<>();
        for (var exerciseDto : er.getPossibleExercises()) {
            Exercise exercise = Exercise.builder()
                    .name(exerciseDto.getName())
                    .instructions(exerciseDto.getDescription())
                    .targetMuscleGroups(new HashSet<>(exerciseDto.getPrimaryMuscles()))
                    .build();
            exercises.add(exerciseRepository.save(exercise));
        }

        // Create new equipment
        Equipment equipment = new Equipment();
        equipment.setImageIcon(imageUrl);
        equipment.setName(er.getEquipmentName());
        equipment.setGymId("1");
        equipment.setCategory(er.getCategory());
        equipment.setDescription(er.getDescription());
        equipment.setPossibleExercises(exercises);
        
        // Use the helper method to manage the bidirectional relationship
        equipment.addUser(user);

        // Save the equipment
        Equipment savedEquipment = equipmentRepository.save(equipment);

        return er;
    }

    public String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}