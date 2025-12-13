package com.mentory.ense_proyect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mentory.ense_proyect.model.entity.User;

import java.util.Optional;

import org.jspecify.annotations.NonNull;

@Repository
public interface UserRepository extends JpaRepository <@NonNull User, @NonNull String> {
    Optional<User> findByUsername(String username);
}
