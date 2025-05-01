package com.fittlens.core.dto;

import lombok.Data;
import java.util.Set;

@Data
public class ExerciseResponse {
    private String id;
    private String name;
    private String instructions;
    private Set<String> targetMuscleGroups;
    private Set<EquipmentResponse> requiredEquipment;
}