package com.fittlens.core.dto.ai;

import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
@Data
public class PossibleExercise {
    private String name;
    private String description;

    @JsonProperty("primary_muscles")
    private List<String> primaryMuscles;
}