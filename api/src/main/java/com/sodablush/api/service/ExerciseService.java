package com.sodablush.api.service;

import com.sodablush.api.dto.CodeExerciseResponseDTO;
import com.sodablush.api.dto.ExerciseSubmitRequestDTO;
import com.sodablush.api.dto.ExerciseSubmitResponseDTO;
import com.sodablush.api.repository.UserRepository;
import com.sodablush.api.repository.UserCodeSubmissionRepository;


import com.sodablush.api.model.CodeExercise;
import com.sodablush.api.model.UserCodeSubmission;
import com.sodablush.api.repository.CodeExerciseRepository;

import com.sodablush.api.model.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExerciseService {

    private final CodeExerciseRepository exerciseRepository;
    private final UserCodeSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    public ExerciseService(CodeExerciseRepository exerciseRepository, UserRepository userRepository, UserCodeSubmissionRepository submissionRepository) {
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
    }

    public CodeExerciseResponseDTO getExerciseForDrink(UUID drinkId) {
        //Busco el ejercicio
        CodeExercise ejercicio = exerciseRepository.findByDrinkId(drinkId)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún ejercicio para este trago"));

        //DTO sin datos con la repsuesta
        CodeExerciseResponseDTO dto = new CodeExerciseResponseDTO();
        dto.setId(ejercicio.getId());
        dto.setInstruction(ejercicio.getInstructions());
        dto.setStarterCode(ejercicio.getStarterCode());
        dto.setOutputHtml(ejercicio.getOutputHtml());
        dto.setOutputCss(ejercicio.getOutputCss());
        dto.setHints(ejercicio.getHints());
        
        return dto;
    }
    

    public ExerciseSubmitResponseDTO submitExercise(UUID userId, UUID exerciseId, ExerciseSubmitRequestDTO request) {
        
        // buscamos el ejercicio y su respuesta
        CodeExercise ejercicio = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));

        User usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        //comparacion del codigo usuario y expectedCss
        String codigoUsuario = request.getSubmittedCode().trim().replaceAll("\\s+", " ");
        String codigoCorrecto = ejercicio.getExpectedCss().trim().replaceAll("\\s+", " ");
        
        boolean esCorrecto = codigoUsuario.equalsIgnoreCase(codigoCorrecto);

        // se guarda el intento USER_CODE_SUBMISSIONS
        UserCodeSubmission nuevoIntento = new UserCodeSubmission();
        nuevoIntento.setId(UUID.randomUUID());
        nuevoIntento.setUser(usuario);
          //drink_id lo sacamos del ejercicio
        nuevoIntento.setDrink(ejercicio.getDrink()); 
        nuevoIntento.setSubmittedCode(request.getSubmittedCode());
        nuevoIntento.setIsCorrect(esCorrecto);
        nuevoIntento.setCreatedAt(java.time.LocalDateTime.now());

        submissionRepository.save(nuevoIntento);

        ExerciseSubmitResponseDTO response = new ExerciseSubmitResponseDTO();
        response.setCorrect(esCorrecto);
        
        if (esCorrecto) {
            response.setFeedbackMessage("¡Perfecto! Tu código CSS es correcto.");
        } else {
            response.setFeedbackMessage("Casi lo tienes. Revisa la sintaxis e inténtalo de nuevo.");
        }

        return response;
    }

}