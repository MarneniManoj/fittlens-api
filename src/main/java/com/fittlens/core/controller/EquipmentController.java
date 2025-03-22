package com.fittlens.core.controller;

import com.fittlens.core.dto.CreateEquipmentRequest;
import com.fittlens.core.dto.EquipmentRequest;
import com.fittlens.core.dto.EquipmentResponse;
import com.fittlens.core.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Autowired
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
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
} 