package com.fittlens.core.repository;

import com.fittlens.core.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, String> {
    List<Equipment> findByUserUuid(String userId);
    List<Equipment> findByUserUuidAndGymId(String userUuid, String gymId);
} 