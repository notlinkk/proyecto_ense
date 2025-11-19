package com.mentory.ense_proyect.repository;

import com.mentory.ense_proyect.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.jspecify.annotations.NonNull;

@Repository
public interface UserRepository extends JpaRepository <@NonNull User, @NonNull String> {
    Optional<User> findByUsername(String username);
}
