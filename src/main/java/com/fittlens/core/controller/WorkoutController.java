package com.fittlens.core.controller;

import com.fittlens.core.dto.WorkoutResponse;
import com.fittlens.core.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    @Autowired
    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping("/today")
    public ResponseEntity<WorkoutResponse> getTodaysWorkout(
            @RequestHeader("User-Id") String userId) {
        WorkoutResponse workout = workoutService.getTodaysWorkout(userId);
        return ResponseEntity.ok(workout);
    }

    @GetMapping("/{workoutId}")
    public ResponseEntity<WorkoutResponse> getWorkout(
            @RequestHeader("User-Id") String userId,
            @PathVariable String workoutId) {
        WorkoutResponse workout = workoutService.getWorkout(userId, workoutId);
        return ResponseEntity.ok(workout);
    }

    @GetMapping("/history")
    public ResponseEntity<List<WorkoutResponse>> getWorkoutHistory(
            @RequestHeader("User-Id") String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
                LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
                LocalDateTime endDate,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        
        List<WorkoutResponse> history = workoutService.getWorkoutHistory(
            userId, startDate, endDate, limit, offset);
        return ResponseEntity.ok(history);
    }
} 