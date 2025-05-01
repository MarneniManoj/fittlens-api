package com.fittlens.core.dto.ai;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EquipmentRecognitionResponse {

    @JsonProperty("equipment_name")
    private String equipmentName;
    
    @JsonProperty("category")
    private String category;

    @JsonProperty("description")
    private String description;

    @JsonProperty("possible_exercises")
    private List<PossibleExercise> possibleExercises;
}

