package com.fittlens.core.controller;

import com.fittlens.core.dto.WorkoutResponse;
import com.fittlens.core.service.WorkoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class WorkoutControllerTest {

    @Mock
    private WorkoutService workoutService;

    @InjectMocks
    private WorkoutController workoutController;

    private static final String USER_ID = "test-user-id";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTodaysWorkout_Success() {
        // Arrange
        WorkoutResponse expectedWorkout = new WorkoutResponse();
        expectedWorkout.setId("test-workout-id");
        when(workoutService.getTodaysWorkout(USER_ID)).thenReturn(expectedWorkout);

        // Act
        ResponseEntity<WorkoutResponse> response = workoutController.getTodaysWorkout(USER_ID);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedWorkout, response.getBody());
        verify(workoutService).getTodaysWorkout(USER_ID);
    }

    @Test
    void getWorkout_Success() {
        // Arrange
        String workoutId = "test-workout-id";
        WorkoutResponse expectedWorkout = new WorkoutResponse();
        expectedWorkout.setId(workoutId);
        when(workoutService.getWorkout(USER_ID, workoutId)).thenReturn(expectedWorkout);

        // Act
        ResponseEntity<WorkoutResponse> response = workoutController.getWorkout(USER_ID, workoutId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedWorkout, response.getBody());
        verify(workoutService).getWorkout(USER_ID, workoutId);
    }

    @Test
    void getWorkoutHistory_Success() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<WorkoutResponse> expectedWorkouts = Arrays.asList(
            new WorkoutResponse(), new WorkoutResponse()
        );

        when(workoutService.getWorkoutHistory(eq(USER_ID), any(), any(), eq(20), eq(0)))
            .thenReturn(expectedWorkouts);

        // Act
        ResponseEntity<List<WorkoutResponse>> response = workoutController.getWorkoutHistory(
            USER_ID, startDate, endDate, 20, 0);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedWorkouts, response.getBody());
        verify(workoutService).getWorkoutHistory(USER_ID, startDate, endDate, 20, 0);
    }

    @Test
    void getWorkoutHistory_WithDefaultDates_Success() {
        // Arrange
        List<WorkoutResponse> expectedWorkouts = Arrays.asList(
            new WorkoutResponse(), new WorkoutResponse()
        );

        when(workoutService.getWorkoutHistory(eq(USER_ID), any(), any(), eq(20), eq(0)))
            .thenReturn(expectedWorkouts);

        // Act
        ResponseEntity<List<WorkoutResponse>> response = workoutController.getWorkoutHistory(
            USER_ID, null, null, 20, 0);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedWorkouts, response.getBody());
        verify(workoutService).getWorkoutHistory(eq(USER_ID), any(), any(), eq(20), eq(0));
    }
} 