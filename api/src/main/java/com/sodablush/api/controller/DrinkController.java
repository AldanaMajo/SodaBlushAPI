package com.sodablush.api.controller;

import com.sodablush.api.dto.DrinkDetailResponseDTO;
import com.sodablush.api.dto.CompleteDrinkResponseDTO;
import com.sodablush.api.dto.TestResponseDTO;
import com.sodablush.api.model.User;
import com.sodablush.api.service.DrinkService;
import com.sodablush.api.service.ExerciseService;
import com.sodablush.api.service.CurrentUserService;
import com.sodablush.api.service.ProgressService;
import com.sodablush.api.service.TestService;
import com.sodablush.api.dto.CodeExerciseResponseDTO;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/drinks")
public class DrinkController {

    private final DrinkService drinkService;
    private final ExerciseService exerciseService;
    private final TestService testService;
    private final ProgressService progressService;
    private final CurrentUserService currentUserService;

    public DrinkController(DrinkService drinkService, ExerciseService exerciseService,
            TestService testService, ProgressService progressService, CurrentUserService currentUserService) {
        this.drinkService = drinkService;
        this.exerciseService = exerciseService;
        this.testService = testService;
        this.progressService = progressService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/{id}")
    public DrinkDetailResponseDTO getDrinkDetails(@PathVariable UUID id) {
        return drinkService.getDrinkDetails(id);
    }

    @GetMapping("/{drinkId}/exercises")
    public CodeExerciseResponseDTO getExercise(@PathVariable UUID drinkId) {
        return exerciseService.getExerciseForDrink(drinkId);
    }

    /** Mini-test del trago (preguntas sin respuestas correctas). */
    @GetMapping("/{drinkId}/test")
    public TestResponseDTO getTest(@PathVariable UUID drinkId) {
        return testService.getTestForDrink(drinkId);
    }

    /**
     * Marca un trago como completado (para tragos de teoria/sintaxis,
     * que no tienen test ni ejercicio que los complete automaticamente).
     */
    @PostMapping("/{drinkId}/complete")
    public CompleteDrinkResponseDTO completeDrink(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID drinkId) {
        User usuario = currentUserService.requireActiveUser(jwt);
        return progressService.completeDrink(usuario, drinkId);
    }
}
