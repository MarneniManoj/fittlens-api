package com.fittlens.core.controller;

import com.fittlens.core.dto.CreateEquipmentRequest;
import com.fittlens.core.dto.EquipmentRequest;
import com.fittlens.core.dto.EquipmentResponse;
import com.fittlens.core.service.EquipmentService;
import com.fittlens.core.service.OpenAIService;
import com.fittlens.core.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;

    private final S3Service s3Service;
    private final OpenAIService openAIService;

    @Autowired
    public EquipmentController(EquipmentService equipmentService, S3Service s3Service, OpenAIService openAIService) {
        this.equipmentService = equipmentService;
        this.s3Service = s3Service;
        this.openAIService = openAIService;
    }

    @PostMapping("/create")
    public ResponseEntity<EquipmentResponse> createEquipment(
            @RequestBody CreateEquipmentRequest request,
            @RequestHeader("User-Id") String userId) {
        EquipmentResponse response = equipmentService.createEquipment(request, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<EquipmentResponse> addEquipment(
            @ModelAttribute EquipmentRequest request,
            @RequestHeader("User-Id") String userId) {
        EquipmentResponse response = equipmentService.addEquipment(request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EquipmentResponse>> listEquipment(
            @RequestHeader("User-Id") String userId,
            @RequestParam(required = false) String gymId) {
        List<EquipmentResponse> equipment = equipmentService.listEquipment(userId, gymId);
        return ResponseEntity.ok(equipment);
    }

    @DeleteMapping("/{equipmentId}")
    public ResponseEntity<Void> deleteEquipment(
            @PathVariable String equipmentId,
            @RequestHeader("User-Id") String userId) {
        equipmentService.deleteEquipment(equipmentId, userId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/generate-upload-url")
    public Map<String, Object> generateUploadUrl(@RequestParam String userId) {
        Map<String, String> urls = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            String filename = UUID.randomUUID() + ".jpg";
            String filePath = "uploads/" + userId + "/" + filename;
            String url = s3Service.generatePresignedUrl(userId, filename);
            urls.put(filePath, url);
        }

        return Map.of("uploadUrls", urls);
    }

    @PostMapping("/analyze")
    public String analyzeEquipment(@RequestParam String imageUrl){
        try {
            return openAIService.recognizeEquipmentFromImageUrl(imageUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
} 