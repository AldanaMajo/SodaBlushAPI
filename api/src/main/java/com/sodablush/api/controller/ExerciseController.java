package com.sodablush.api.controller;

import com.sodablush.api.dto.ExerciseSubmitRequestDTO;
import com.sodablush.api.dto.ExerciseSubmitResponseDTO;
import com.sodablush.api.model.User;
import com.sodablush.api.service.CurrentUserService;
import com.sodablush.api.service.ExerciseService;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final CurrentUserService currentUserService;

    public ExerciseController(ExerciseService exerciseService, CurrentUserService currentUserService) {
        this.exerciseService = exerciseService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/{exerciseId}/submit")
    public ExerciseSubmitResponseDTO submitCode(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID exerciseId, 
            @Valid @RequestBody ExerciseSubmitRequestDTO requestBody) {

        User usuario = currentUserService.requireActiveUser(jwt);
        return exerciseService.submitExercise(usuario, exerciseId, requestBody);
    }
}
