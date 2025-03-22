package com.fittlens.core.service.impl;

import com.fittlens.core.dto.ExerciseResponse;
import com.fittlens.core.dto.WorkoutResponse;
import com.fittlens.core.model.Exercise;
import com.fittlens.core.model.WorkoutSession;
import com.fittlens.core.repository.WorkoutSessionRepository;
import com.fittlens.core.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutSessionRepository workoutSessionRepository;

    @Autowired
    public WorkoutServiceImpl(WorkoutSessionRepository workoutSessionRepository) {
        this.workoutSessionRepository = workoutSessionRepository;
    }

    @Override
    public WorkoutResponse getTodaysWorkout(String userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        
        List<WorkoutSession> todaysWorkouts = workoutSessionRepository
            .findByUserUuidAndScheduledDateBetween(userId, startOfDay, endOfDay);
        
        return todaysWorkouts.isEmpty() ? null : 
            convertToWorkoutResponse(todaysWorkouts.get(0));
    }

    @Override
    public WorkoutResponse getWorkout(String userId, String workoutId) {
        WorkoutSession workout = workoutSessionRepository.findByUserUuidAndId(userId, workoutId);
        if (workout == null) {
            throw new RuntimeException("Workout not found");
        }
        return convertToWorkoutResponse(workout);
    }

    @Override
    public List<WorkoutResponse> getWorkoutHistory(
            String userId, LocalDateTime startDate, LocalDateTime endDate, 
            int limit, int offset) {
        List<WorkoutSession> workouts = workoutSessionRepository
            .findByUserUuidAndScheduledDateBetween(userId, startDate, endDate);
        
        return workouts.stream()
            .skip(offset)
            .limit(limit)
            .map(this::convertToWorkoutResponse)
            .collect(Collectors.toList());
    }

    private WorkoutResponse convertToWorkoutResponse(WorkoutSession workout) {
        WorkoutResponse response = new WorkoutResponse();
        response.setId(workout.getId());
        response.setScheduledDate(workout.getScheduledDate());
        response.setCompletedDate(workout.getCompletedDate());
        response.setCompleted(workout.isCompleted());
        response.setExercises(workout.getExercises().stream()
            .map(this::convertToExerciseResponse)
            .collect(Collectors.toSet()));
        return response;
    }

    public ExerciseResponse convertToExerciseResponse(Exercise exercise) {
        ExerciseResponse response = new ExerciseResponse();
        response.setId(exercise.getId());
        response.setName(exercise.getName());
        response.setInstructions(exercise.getInstructions());
        response.setTargetMuscleGroups(exercise.getTargetMuscleGroups());
        return response;
    }
} 