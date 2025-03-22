package com.fittlens.core.service.impl;

import com.fittlens.core.dto.ExerciseResponse;
import com.fittlens.core.dto.WorkoutResponse;
import com.fittlens.core.model.Exercise;
import com.fittlens.core.model.User;
import com.fittlens.core.model.WorkoutSession;
import com.fittlens.core.repository.WorkoutSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class WorkoutServiceImplTest {

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @InjectMocks
    private WorkoutServiceImpl workoutService;

    private static final String USER_ID = "test-user-id";
    private User testUser;
    private Exercise testExercise1;
    private Exercise testExercise2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setupTestData();
    }

    private void setupTestData() {
        testUser = new User();
        testUser.setUuid(USER_ID);
        testUser.setName("TestUser");

        testExercise1 = new Exercise();
        testExercise1.setId("exercise-1");
        testExercise1.setName("Push-ups");
        testExercise1.setInstructions("Do push-ups");
        testExercise1.setTargetMuscleGroups(new HashSet<>(Arrays.asList("chest", "triceps")));

        testExercise2 = new Exercise();
        testExercise2.setId("exercise-2");
        testExercise2.setName("Pull-ups");
        testExercise2.setInstructions("Do pull-ups");
        testExercise2.setTargetMuscleGroups(new HashSet<>(Arrays.asList("back", "biceps")));
    }

    private WorkoutSession createTestWorkoutSession(String id, LocalDateTime scheduledDate) {
        WorkoutSession workout = new WorkoutSession();
        workout.setId(id);
        workout.setUser(testUser);
        workout.setScheduledDate(scheduledDate);
        Set<Exercise> exercises = new HashSet<>(Arrays.asList(testExercise1, testExercise2));
        workout.setExercises(exercises);
        return workout;
    }

    @Test
    void getTodaysWorkout_Success() {
        // Arrange
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        
        WorkoutSession todaysWorkout = createTestWorkoutSession("workout-1", startOfDay.plusHours(10));
        
        when(workoutSessionRepository.findByUserUuidAndScheduledDateBetween(
            eq(USER_ID), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(todaysWorkout));

        // Act
        WorkoutResponse response = workoutService.getTodaysWorkout(USER_ID);

        // Assert
        assertNotNull(response);
        assertEquals(todaysWorkout.getId(), response.getId());
        assertEquals(2, response.getExercises().size());
        verify(workoutSessionRepository).findByUserUuidAndScheduledDateBetween(
            eq(USER_ID), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getTodaysWorkout_NoWorkoutFound_ReturnsNull() {
        // Arrange
        when(workoutSessionRepository.findByUserUuidAndScheduledDateBetween(
            eq(USER_ID), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList());

        // Act
        WorkoutResponse response = workoutService.getTodaysWorkout(USER_ID);

        // Assert
        assertNull(response);
        verify(workoutSessionRepository).findByUserUuidAndScheduledDateBetween(
            eq(USER_ID), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getWorkout_Success() {
        // Arrange
        String workoutId = "workout-1";
        WorkoutSession workout = createTestWorkoutSession(workoutId, LocalDateTime.now());
        
        when(workoutSessionRepository.findByUserUuidAndId(USER_ID, workoutId))
            .thenReturn(workout);

        // Act
        WorkoutResponse response = workoutService.getWorkout(USER_ID, workoutId);

        // Assert
        assertNotNull(response);
        assertEquals(workoutId, response.getId());
        assertEquals(2, response.getExercises().size());
        verify(workoutSessionRepository).findByUserUuidAndId(USER_ID, workoutId);
    }

    @Test
    void getWorkout_NotFound_ThrowsException() {
        // Arrange
        String workoutId = "non-existent-workout";
        when(workoutSessionRepository.findByUserUuidAndId(USER_ID, workoutId))
            .thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            workoutService.getWorkout(USER_ID, workoutId));
        verify(workoutSessionRepository).findByUserUuidAndId(USER_ID, workoutId);
    }

    @Test
    void getWorkoutHistory_Success() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        List<WorkoutSession> workouts = Arrays.asList(
            createTestWorkoutSession("workout-1", startDate.plusDays(1)),
            createTestWorkoutSession("workout-2", startDate.plusDays(2))
        );
        
        when(workoutSessionRepository.findByUserUuidAndScheduledDateBetween(
            USER_ID, startDate, endDate))
            .thenReturn(workouts);

        // Act
        List<WorkoutResponse> response = workoutService.getWorkoutHistory(
            USER_ID, startDate, endDate, 20, 0);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("workout-1", response.get(0).getId());
        assertEquals("workout-2", response.get(1).getId());
        verify(workoutSessionRepository).findByUserUuidAndScheduledDateBetween(
            USER_ID, startDate, endDate);
    }

    @Test
    void getWorkoutHistory_WithPagination_Success() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        List<WorkoutSession> workouts = Arrays.asList(
            createTestWorkoutSession("workout-1", startDate.plusDays(1)),
            createTestWorkoutSession("workout-2", startDate.plusDays(2)),
            createTestWorkoutSession("workout-3", startDate.plusDays(3))
        );
        
        when(workoutSessionRepository.findByUserUuidAndScheduledDateBetween(
            USER_ID, startDate, endDate))
            .thenReturn(workouts);

        // Act
        List<WorkoutResponse> response = workoutService.getWorkoutHistory(
            USER_ID, startDate, endDate, 2, 1);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("workout-2", response.get(0).getId());
        assertEquals("workout-3", response.get(1).getId());
        verify(workoutSessionRepository).findByUserUuidAndScheduledDateBetween(
            USER_ID, startDate, endDate);
    }

    @Test
    void convertToExerciseResponse_Success() {
        // Arrange
        Exercise exercise = testExercise1;

        // Act
        ExerciseResponse response = workoutService.convertToExerciseResponse(exercise);

        // Assert
        assertNotNull(response);
        assertEquals(exercise.getId(), response.getId());
        assertEquals(exercise.getName(), response.getName());
        assertEquals(exercise.getInstructions(), response.getInstructions());
        assertEquals(exercise.getTargetMuscleGroups(), response.getTargetMuscleGroups());
    }
} 