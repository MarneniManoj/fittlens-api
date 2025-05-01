package com.fittlens.core.dto.ai;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EquipmentRecognitionResponse {

    @JsonProperty("equipment_name")
    private String equipmentName;
    
    private String category;
    
    @JsonProperty("primary_muscles")
    private List<String> primaryMuscles;
    
    private String description;
}
