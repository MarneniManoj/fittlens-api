package com.fittlens.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "equipment")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String imageIcon;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    @Column(name = "gym_id")
    private String gymId;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "user_equipment",
        joinColumns = @JoinColumn(name = "equipment_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "equipment_exercises",
        joinColumns = @JoinColumn(name = "equipment_id"),
        inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    private Set<Exercise> possibleExercises = new HashSet<>();
    
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    // Helper method to manage bidirectional relationship
    public void addUser(User user) {
        users.add(user);
        user.getEquipments().add(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.getEquipments().remove(this);
    }

    @PrePersist
    protected void onCreate() {
        createDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipment)) return false;
        Equipment equipment = (Equipment) o;
        return id != null && id.equals(equipment.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 