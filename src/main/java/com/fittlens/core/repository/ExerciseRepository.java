package com.fittlens.core.repository;

import com.fittlens.core.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Set;

public interface ExerciseRepository extends JpaRepository<Exercise, String> {
    Set<Exercise> findByRequiredEquipmentIn(Set<String> equipmentIds);
} 