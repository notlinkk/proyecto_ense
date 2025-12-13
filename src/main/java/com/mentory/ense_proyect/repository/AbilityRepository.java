package com.mentory.ense_proyect.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentory.ense_proyect.model.entity.Ability;

@Repository
public interface AbilityRepository extends JpaRepository <@NonNull Ability, @NonNull String> {

}
