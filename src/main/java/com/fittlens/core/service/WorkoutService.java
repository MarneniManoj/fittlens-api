package com.fittlens.core.service;

import com.fittlens.core.dto.WorkoutResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface WorkoutService {
    WorkoutResponse getTodaysWorkout(String userId);
    WorkoutResponse getWorkout(String userId, String workoutId);
    List<WorkoutResponse> getWorkoutHistory(String userId, LocalDateTime startDate, 
                                          LocalDateTime endDate, int limit, int offset);
} 