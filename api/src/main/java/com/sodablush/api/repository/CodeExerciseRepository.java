package com.sodablush.api.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sodablush.api.model.CodeExercise;

@Repository 
public interface CodeExerciseRepository extends JpaRepository<CodeExercise, UUID> {
    Optional<CodeExercise> findByDrinkId(UUID drinkId);
}
