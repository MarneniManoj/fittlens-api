package com.fittlens.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;
    
    @Column(unique = true)
    private String name;
    
    private String deviceId;
    private String email;
    private String token;
    
    @Column(nullable = true)
    private String password;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "users")
    private Set<Equipment> equipments = new HashSet<>();

    // Helper method to manage bidirectional relationship
    public void addEquipment(Equipment equipment) {
        equipments.add(equipment);
        equipment.getUsers().add(this);
    }

    public void removeEquipment(Equipment equipment) {
        equipments.remove(equipment);
        equipment.getUsers().remove(this);
    }

    @Override
    public Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return uuid != null && uuid.equals(user.getUuid());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 