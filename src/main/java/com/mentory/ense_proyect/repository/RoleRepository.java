package com.mentory.ense_proyect.repository;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentory.ense_proyect.model.entity.Role;

@Repository
@NullMarked
public interface RoleRepository extends JpaRepository <@NonNull Role, @NonNull String> {
    Role findByRolename(@NonNull String rolename);
}