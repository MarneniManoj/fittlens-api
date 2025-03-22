package com.fittlens.core.dto;

import lombok.Data;
import java.util.Set;

@Data
public class EquipmentResponse {
    private String id;
    private String name;
    private String imageIcon;
    private Set<ExerciseResponse> possibleExercises;
    private String userId;
}