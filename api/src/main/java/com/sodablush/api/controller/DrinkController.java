package com.sodablush.api.controller;

import com.sodablush.api.dto.DrinkDetailResponseDTO;
import com.sodablush.api.service.DrinkService;
import com.sodablush.api.service.ExerciseService;
import com.sodablush.api.dto.CodeExerciseResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/drinks")
public class DrinkController {

    private final DrinkService drinkService;
    private final ExerciseService exerciseService;

    public DrinkController(DrinkService drinkService,  ExerciseService exerciseService ) {
        this.drinkService = drinkService;
        this.exerciseService = exerciseService;
    }

    @GetMapping("/{id}")
    public DrinkDetailResponseDTO getDrinkDetails(@PathVariable UUID id) {
        return drinkService.getDrinkDetails(id);
    }
    @GetMapping("/{drinkId}/exercises")
    public CodeExerciseResponseDTO getExercise(@PathVariable UUID drinkId) {
        return exerciseService.getExerciseForDrink(drinkId);
    }
}