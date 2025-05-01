package com.fittlens.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Entity
@Table(name = "exercises")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String instructions;
    
    @ManyToMany(mappedBy = "possibleExercises")
    private Set<Equipment> requiredEquipment;
    
    @ElementCollection
    @CollectionTable(name = "exercise_muscles", 
        joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "muscle_name")
    private Set<String> targetMuscleGroups;
} 