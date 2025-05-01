package com.fittlens.core.repository;

import com.fittlens.core.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, String> {
    @Query("SELECT e FROM Equipment e JOIN e.users u WHERE u.uuid = :userId")
    List<Equipment> findByUserId(@Param("userId") String userId);
} 