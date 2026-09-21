package com.sodablush.api.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.sodablush.api.model.CodeExercise;

/** Pruebas unitarias del grading de la prueba final (sin base de datos). */
class ExerciseGradingTest {

    private final ExerciseService service = new ExerciseService(null, null, null);

    private CodeExercise exerciseWithExpected(String expectedCss) {
        CodeExercise ejercicio = new CodeExercise();
        ejercicio.setExpectedCss(expectedCss);
        return ejercicio;
    }

    @Test
    void acceptsExactMatch() {
        CodeExercise ejercicio = exerciseWithExpected("div:hover { color: red; }");
        assertTrue(service.grade(ejercicio, "div:hover { color: red; }"));
    }

    @Test
    void ignoresWhitespaceAndCase() {
        CodeExercise ejercicio = exerciseWithExpected("div:hover { color: red; }");
        assertTrue(service.grade(ejercicio, "  DIV:HOVER {\n   color:   red;\n}  "));
    }

    @Test
    void rejectsWrongCss() {
        CodeExercise ejercicio = exerciseWithExpected("div:hover { color: red; }");
        assertFalse(service.grade(ejercicio, "div:hover { color: blue; }"));
    }

    @Test
    void validationRulesRequiredContains() {
        CodeExercise ejercicio = new CodeExercise();
        ejercicio.setValidationRules(Map.of("requiredContains", List.of("display: flex", "gap:")));

        assertTrue(service.grade(ejercicio, ".box { display: flex; gap: 8px; }"));
        assertFalse(service.grade(ejercicio, ".box { display: grid; gap: 8px; }"));
    }

    @Test
    void validationRulesForbidden() {
        CodeExercise ejercicio = new CodeExercise();
        ejercicio.setValidationRules(Map.of(
                "requiredContains", List.of("display: flex"),
                "forbidden", List.of("float")));

        assertTrue(service.grade(ejercicio, ".box { display: flex; }"));
        assertFalse(service.grade(ejercicio, ".box { display: flex; float: left; }"));
    }

    @Test
    void nullSubmissionIsRejected() {
        CodeExercise ejercicio = exerciseWithExpected("a { color: red; }");
        assertFalse(service.grade(ejercicio, null));
    }
}
