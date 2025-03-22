package com.fittlens.core.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class WorkoutResponse {
    private String id;
    private Set<ExerciseResponse> exercises;
    private LocalDateTime scheduledDate;
    private LocalDateTime completedDate;
    private boolean isCompleted;
} 