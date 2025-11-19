package com.mentory.ense_proyect.repository;

import com.mentory.ense_proyect.model.Module;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.jspecify.annotations.NonNull;

@Repository
public interface ModuleRepository extends JpaRepository <@NonNull Module, @NonNull String> {
}
