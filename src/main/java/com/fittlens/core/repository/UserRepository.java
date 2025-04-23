package com.fittlens.core.repository;

import com.fittlens.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByName(String name);
    boolean existsByName(String name);
    Optional<User> findByEmail(String email);
} 