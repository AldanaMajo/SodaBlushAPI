package com.sodablush.api.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sodablush.api.dto.AnswerRequestDTO;
import com.sodablush.api.dto.AnswerResponseDTO;
import com.sodablush.api.dto.FinishAttemptResponseDTO;
import com.sodablush.api.dto.StartAttemptResponseDTO;
import com.sodablush.api.model.User;
import com.sodablush.api.service.CurrentUserService;
import com.sodablush.api.service.TestService;

import jakarta.validation.Valid;

/** Mini-test (segundo trago): intentos, respuestas y resultado. */
@RestController
@RequestMapping("/api/tests")
public class TestController {

    private final TestService testService;
    private final CurrentUserService currentUserService;

    public TestController(TestService testService, CurrentUserService currentUserService) {
        this.testService = testService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/{testId}/attempts")
    public StartAttemptResponseDTO startAttempt(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID testId) {
        User usuario = currentUserService.requireActiveUser(jwt);
        return testService.startAttempt(usuario, testId);
    }

    @PostMapping("/attempts/{attemptId}/answers")
    public AnswerResponseDTO submitAnswer(@AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID attemptId,
            @Valid @RequestBody AnswerRequestDTO request) {
        User usuario = currentUserService.requireActiveUser(jwt);
        return testService.submitAnswer(usuario, attemptId, request);
    }

    @PostMapping("/attempts/{attemptId}/finish")
    public FinishAttemptResponseDTO finishAttempt(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID attemptId) {
        User usuario = currentUserService.requireActiveUser(jwt);
        return testService.finishAttempt(usuario, attemptId);
    }
}
