package com.mentory.ense_proyect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentory.ense_proyect.model.entity.Module;

import org.jspecify.annotations.NonNull;

@Repository
public interface ModuleRepository extends JpaRepository <@NonNull Module, @NonNull String> {
}
