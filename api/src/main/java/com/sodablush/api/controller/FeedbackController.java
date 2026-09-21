package com.sodablush.api.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sodablush.api.dto.FeedbackRequestDTO;
import com.sodablush.api.model.Feedback;
import com.sodablush.api.model.User;
import com.sodablush.api.repository.FeedbackRepository;
import com.sodablush.api.service.CurrentUserService;

import jakarta.validation.Valid;

/** Ayuda y comentarios (seccion de ajustes de la app). */
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final CurrentUserService currentUserService;

    public FeedbackController(FeedbackRepository feedbackRepository, CurrentUserService currentUserService) {
        this.feedbackRepository = feedbackRepository;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> submitFeedback(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody FeedbackRequestDTO request) {
        User usuario = currentUserService.requireActiveUser(jwt);

        Feedback feedback = new Feedback();
        feedback.setId(UUID.randomUUID());
        feedback.setUser(usuario);
        feedback.setType(request.getType());
        feedback.setMessage(request.getMessage());
        feedback.setScreenshotUrl(request.getScreenshotUrl());
        feedback.setStatus("NEW");
        feedback.setCreatedAt(LocalDateTime.now());
        feedbackRepository.save(feedback);

        return Map.of("id", feedback.getId(), "message", "¡Gracias por tu feedback!");
    }
}
