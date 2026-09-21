package com.sodablush.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sodablush.api.dto.CodeExerciseResponseDTO;
import com.sodablush.api.dto.CompleteDrinkResponseDTO;
import com.sodablush.api.dto.ExerciseSubmitRequestDTO;
import com.sodablush.api.dto.ExerciseSubmitResponseDTO;
import com.sodablush.api.exception.NotFoundException;
import com.sodablush.api.model.CodeExercise;
import com.sodablush.api.model.User;
import com.sodablush.api.model.UserCodeSubmission;
import com.sodablush.api.repository.CodeExerciseRepository;
import com.sodablush.api.repository.UserCodeSubmissionRepository;

/**
 * Prueba final (cuarto trago): el usuario escribe CSS y la API lo valida.
 * Si CODE_EXERCISES.validation_rules trae reglas, se usan; si no,
 * se compara contra expected_css normalizando espacios.
 */
@Service
public class ExerciseService {

    private final CodeExerciseRepository exerciseRepository;
    private final UserCodeSubmissionRepository submissionRepository;
    private final ProgressService progressService;

    public ExerciseService(CodeExerciseRepository exerciseRepository,
            UserCodeSubmissionRepository submissionRepository,
            ProgressService progressService) {
        this.exerciseRepository = exerciseRepository;
        this.submissionRepository = submissionRepository;
        this.progressService = progressService;
    }

    public CodeExerciseResponseDTO getExerciseForDrink(UUID drinkId) {
        CodeExercise ejercicio = exerciseRepository.findByDrinkId(drinkId)
                .orElseThrow(() -> new NotFoundException("No se encontró ningún ejercicio para este trago"));

        //DTO sin la respuesta esperada
        CodeExerciseResponseDTO dto = new CodeExerciseResponseDTO();
        dto.setId(ejercicio.getId());
        dto.setInstruction(ejercicio.getInstructions());
        dto.setStarterCode(ejercicio.getStarterCode());
        dto.setOutputHtml(ejercicio.getOutputHtml());
        dto.setOutputCss(ejercicio.getOutputCss());
        dto.setHints(ejercicio.getHints());

        return dto;
    }

    @Transactional
    public ExerciseSubmitResponseDTO submitExercise(User usuario, UUID exerciseId, ExerciseSubmitRequestDTO request) {
        CodeExercise ejercicio = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException("Ejercicio no encontrado"));

        boolean esCorrecto = grade(ejercicio, request.getSubmittedCode());

        // Se guarda el intento en USER_CODE_SUBMISSIONS
        UserCodeSubmission nuevoIntento = new UserCodeSubmission();
        nuevoIntento.setId(UUID.randomUUID());
        nuevoIntento.setUser(usuario);
        nuevoIntento.setDrink(ejercicio.getDrink());
        nuevoIntento.setSubmittedCode(request.getSubmittedCode());
        nuevoIntento.setIsCorrect(esCorrecto);
        nuevoIntento.setCreatedAt(LocalDateTime.now());
        submissionRepository.save(nuevoIntento);

        ExerciseSubmitResponseDTO response = new ExerciseSubmitResponseDTO();
        response.setCorrect(esCorrecto);
        response.setNewAchievements(List.of());

        if (esCorrecto) {
            response.setFeedbackMessage("¡Perfecto! Tu código CSS es correcto.");
            if (ejercicio.getDrink() != null) {
                CompleteDrinkResponseDTO avance = progressService.completeDrink(usuario, ejercicio.getDrink().getId());
                response.setDrinkCompleted(true);
                response.setCanCompleted(avance.isCanCompleted());
                response.setNextDrinkId(avance.getNextDrinkId());
                response.setNewAchievements(avance.getNewAchievements());
            }
        } else {
            response.setFeedbackMessage("Casi lo tienes. Revisa la sintaxis e inténtalo de nuevo.");
        }

        return response;
    }

    /**
     * Valida el codigo enviado.
     * validation_rules soporta:
     *   { "requiredContains": ["display: flex", ...], "forbidden": ["float", ...] }
     * Sin reglas: comparacion normalizada contra expected_css.
     */
    boolean grade(CodeExercise ejercicio, String submittedCode) {
        String enviado = normalize(submittedCode);

        Map<String, Object> reglas = ejercicio.getValidationRules();
        if (reglas != null && (reglas.containsKey("requiredContains") || reglas.containsKey("forbidden"))) {
            for (String requerido : asStringList(reglas.get("requiredContains"))) {
                if (!enviado.contains(normalize(requerido))) {
                    return false;
                }
            }
            for (String prohibido : asStringList(reglas.get("forbidden"))) {
                if (enviado.contains(normalize(prohibido))) {
                    return false;
                }
            }
            return true;
        }

        String esperado = ejercicio.getExpectedCss();
        return esperado != null && enviado.equals(normalize(esperado));
    }

    private String normalize(String codigo) {
        return codigo == null ? "" : codigo.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    private List<String> asStringList(Object valor) {
        List<String> lista = new ArrayList<>();
        if (valor instanceof List<?> elementos) {
            for (Object elemento : elementos) {
                if (elemento != null) {
                    lista.add(elemento.toString());
                }
            }
        }
        return lista;
    }
}
