package com.sodablush.api.controller;

import com.sodablush.api.dto.ExerciseSubmitRequestDTO;
import com.sodablush.api.dto.ExerciseSubmitResponseDTO;
import com.sodablush.api.service.ExerciseService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping("/{exerciseId}/submit")
    public ExerciseSubmitResponseDTO submitCode(
            @PathVariable UUID exerciseId, 
            @RequestBody ExerciseSubmitRequestDTO requestBody) { //lee el json que envia el usuario (de la respuesta)
        
        //Auth0
        UUID simulatedUserId = UUID.fromString("UUID-USERS");
        
        return exerciseService.submitExercise(simulatedUserId, exerciseId, requestBody);
    }
}