package com.fittlens.core.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "workout_sessions")
public class WorkoutSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToMany
    @JoinTable(
        name = "workout_exercises",
        joinColumns = @JoinColumn(name = "workout_id"),
        inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    private Set<Exercise> exercises;
    
    private LocalDateTime scheduledDate;
    private LocalDateTime completedDate;
    private boolean isCompleted;
}