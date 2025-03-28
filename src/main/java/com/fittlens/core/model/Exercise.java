package com.fittlens.core.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String name;
    private String instructions;
    
    @ManyToMany(mappedBy = "possibleExercises")
    private Set<Equipment> requiredEquipment;
    
    @ElementCollection
    @CollectionTable(name = "exercise_muscles", 
        joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "muscle_name")
    private Set<String> targetMuscleGroups;
} 