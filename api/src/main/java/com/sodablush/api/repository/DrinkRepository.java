package com.sodablush.api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sodablush.api.model.Drink;

@Repository 
public interface DrinkRepository extends JpaRepository<Drink, UUID> {

    List<Drink> findByCanIdOrderByStepOrderAsc(UUID canId);
}
