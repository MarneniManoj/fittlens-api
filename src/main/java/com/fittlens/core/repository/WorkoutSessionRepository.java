package com.fittlens.core.repository;

import com.fittlens.core.model.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, String> {
    List<WorkoutSession> findByUserUuidAndScheduledDateBetween(
        String userId, LocalDateTime startDate, LocalDateTime endDate);
    
    WorkoutSession findByUserUuidAndId(String userId, String workoutId);
} 