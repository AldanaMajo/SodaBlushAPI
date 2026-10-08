package com.sodablush.api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sodablush.api.dto.AnswerRequestDTO;
import com.sodablush.api.dto.AnswerResponseDTO;
import com.sodablush.api.dto.CompleteDrinkResponseDTO;
import com.sodablush.api.dto.FinishAttemptResponseDTO;
import com.sodablush.api.dto.QuestionDTO;
import com.sodablush.api.dto.StartAttemptResponseDTO;
import com.sodablush.api.dto.TestResponseDTO;
import com.sodablush.api.exception.BadRequestException;
import com.sodablush.api.exception.ForbiddenException;
import com.sodablush.api.exception.NotFoundException;
import com.sodablush.api.model.Question;
import com.sodablush.api.model.Test;
import com.sodablush.api.model.User;
import com.sodablush.api.model.UserTestAnswer;
import com.sodablush.api.model.UserTestAttempt;
import com.sodablush.api.repository.QuestionRepository;
import com.sodablush.api.repository.TestRepository;
import com.sodablush.api.repository.UserTestAnswerRepository;
import com.sodablush.api.repository.UserTestAttemptRepository;

/**
 * Mini-test (segundo trago): flashcards con vidas ("burbujas").
 * Flujo: GET test -> POST attempt -> POST answers -> POST finish.
 */
@Service
public class TestService {

    private static final int DEFAULT_LIVES = 3;
    private static final BigDecimal DEFAULT_PASSING_SCORE = new BigDecimal("70");

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final UserTestAttemptRepository attemptRepository;
    private final UserTestAnswerRepository answerRepository;
    private final ProgressService progressService;

    public TestService(TestRepository testRepository, QuestionRepository questionRepository,
            UserTestAttemptRepository attemptRepository, UserTestAnswerRepository answerRepository,
            ProgressService progressService) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.progressService = progressService;
    }

    /** Test + preguntas de un trago, sin revelar las respuestas correctas. */
    public TestResponseDTO getTestForDrink(UUID drinkId) {
        Test test = testRepository.findByDrinkId(drinkId)
                .orElseThrow(() -> new NotFoundException("Este trago no tiene mini-test"));

        TestResponseDTO dto = new TestResponseDTO();
        dto.setId(test.getId());
        dto.setDrinkId(drinkId);
        dto.setTestType(test.getTestType());
        dto.setInstructions(test.getInstructions());
        dto.setPassingScore(test.getPassingScore());
        dto.setMaxAttempts(test.getMaxAttempts());
        dto.setLivesAllowed(test.getLivesAllowed());

        List<QuestionDTO> preguntas = new ArrayList<>();
        for (Question pregunta : questionRepository.findByTestIdOrderByOrderIndexAsc(test.getId())) {
            QuestionDTO q = new QuestionDTO();
            q.setId(pregunta.getId());
            q.setQuestionType(pregunta.getQuestionType());
            q.setPrompt(pregunta.getPrompt());
            q.setCodeSnippet(pregunta.getCodeSnippet());
            q.setOrderIndex(pregunta.getOrderIndex());
            q.setPoints(pregunta.getPoints());
            q.setOptions(pregunta.getOptions());
            preguntas.add(q);
        }
        dto.setQuestions(preguntas);
        return dto;
    }

    @Transactional
    public StartAttemptResponseDTO startAttempt(User user, UUID testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new NotFoundException("Test no encontrado"));

        UserTestAttempt abierto = attemptRepository
                .findFirstByUserIdAndTestIdAndCompletedAtIsNullOrderByStartedAtDesc(user.getId(), testId)
                .orElse(null);

        if (abierto != null) {
            if (abierto.getLivesRemaining() != null && abierto.getLivesRemaining() <= 0) {
                finishAttempt(user, abierto.getId());
            } else {
                return toStartDto(abierto);
            }
        }

        if (test.getMaxAttempts() != null && test.getMaxAttempts() > 0) {
            long cerrados = attemptRepository
                    .countByUserIdAndTestIdAndCompletedAtIsNotNull(user.getId(), testId);
            if (cerrados >= test.getMaxAttempts()) {
                throw new BadRequestException("Ya no tienes intentos disponibles para este test");
            }
        }

        UserTestAttempt intento = new UserTestAttempt();
        intento.setId(UUID.randomUUID());
        intento.setUser(user);
        intento.setTest(test);
        intento.setDrink(test.getDrink());
        intento.setScore(0);
        intento.setLivesRemaining(test.getLivesAllowed() != null ? test.getLivesAllowed() : DEFAULT_LIVES);
        intento.setStartedAt(LocalDateTime.now());
        attemptRepository.save(intento);

        return toStartDto(intento);
    }

    @Transactional
    public AnswerResponseDTO submitAnswer(User user, UUID attemptId, AnswerRequestDTO request) {
        UserTestAttempt intento = getOwnedOpenAttempt(user, attemptId);

        if (intento.getLivesRemaining() != null && intento.getLivesRemaining() <= 0) {
            throw new BadRequestException("Te quedaste sin gas (sin vidas). Finaliza el intento.");
        }

        Question pregunta = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new NotFoundException("Pregunta no encontrada"));
        if (!pregunta.getTest().getId().equals(intento.getTest().getId())) {
            throw new BadRequestException("La pregunta no pertenece a este test");
        }
        if (answerRepository.existsByAttemptIdAndQuestionId(attemptId, pregunta.getId())) {
            throw new BadRequestException("Ya respondiste esta pregunta en este intento");
        }
        if (request.getSelectedOptionId() == null
                && (request.getAnsweredText() == null || request.getAnsweredText().isBlank())) {
            throw new BadRequestException("Envia selectedOptionId o answeredText");
        }

        boolean esCorrecta = isCorrect(pregunta, request);

        UserTestAnswer respuesta = new UserTestAnswer();
        respuesta.setId(UUID.randomUUID());
        respuesta.setAttempt(intento);
        respuesta.setQuestion(pregunta);
        respuesta.setSelectedOptionId(request.getSelectedOptionId());
        respuesta.setAnsweredText(request.getAnsweredText());
        respuesta.setIsCorrect(esCorrecta);
        respuesta.setTimeSpentSeconds(request.getTimeSpentSeconds());
        answerRepository.save(respuesta);

        if (!esCorrecta && intento.getLivesRemaining() != null) {
            intento.setLivesRemaining(intento.getLivesRemaining() - 1);
            attemptRepository.save(intento);
        }

        AnswerResponseDTO dto = new AnswerResponseDTO();
        dto.setCorrect(esCorrecta);
        dto.setLivesRemaining(intento.getLivesRemaining());
        dto.setExplanation(pregunta.getExplanation());
        dto.setOutOfLives(intento.getLivesRemaining() != null && intento.getLivesRemaining() <= 0);
        return dto;
    }

    @Transactional
    public FinishAttemptResponseDTO finishAttempt(User user, UUID attemptId) {
        UserTestAttempt intento = getOwnedOpenAttempt(user, attemptId);
        Test test = intento.getTest();

        // Puntaje: puntos de respuestas correctas / puntos totales del test
        List<Question> preguntas = questionRepository.findByTestIdOrderByOrderIndexAsc(test.getId());
        List<UserTestAnswer> respuestas = answerRepository.findByAttemptId(attemptId);

        BigDecimal totales = BigDecimal.ZERO;
        for (Question p : preguntas) {
            totales = totales.add(points(p));
        }
        BigDecimal obtenidos = BigDecimal.ZERO;
        for (UserTestAnswer r : respuestas) {
            if (Boolean.TRUE.equals(r.getIsCorrect())) {
                obtenidos = obtenidos.add(points(r.getQuestion()));
            }
        }

        int score = 0;
        if (totales.compareTo(BigDecimal.ZERO) > 0) {
            score = obtenidos.multiply(new BigDecimal("100"))
                    .divide(totales, 0, RoundingMode.HALF_UP)
                    .intValue();
        }

        boolean sinVidas = intento.getLivesRemaining() != null && intento.getLivesRemaining() <= 0;
        boolean aprobado = !sinVidas && score >= passingScore(test).intValue();

        intento.setScore(score);
        intento.setPassed(aprobado);
        intento.setCompletedAt(LocalDateTime.now());
        attemptRepository.save(intento);

        FinishAttemptResponseDTO dto = new FinishAttemptResponseDTO();
        dto.setAttemptId(attemptId);
        dto.setScore(score);
        dto.setPassed(aprobado);
        dto.setLivesRemaining(intento.getLivesRemaining());
        dto.setNewAchievements(List.of());

        if (aprobado && intento.getDrink() != null) {
            CompleteDrinkResponseDTO avance = progressService.completeDrink(user, intento.getDrink().getId());
            dto.setDrinkCompleted(true);
            dto.setNextDrinkId(avance.getNextDrinkId());
            dto.setCanCompleted(avance.isCanCompleted());
            dto.setNewAchievements(avance.getNewAchievements());
        }

        return dto;
    }

    private UserTestAttempt getOwnedOpenAttempt(User user, UUID attemptId) {
        UserTestAttempt intento = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new NotFoundException("Intento no encontrado"));
        if (!intento.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Este intento no es tuyo");
        }
        if (intento.getCompletedAt() != null) {
            throw new BadRequestException("Este intento ya fue finalizado");
        }
        return intento;
    }

    /**
     * Compara contra QUESTIONS.correct_answer:
     * - selectedOptionId: se compara el UUID como texto.
     * - answeredText: comparacion normalizada (trim + lowercase), util para true/false.
     */
    private boolean isCorrect(Question pregunta, AnswerRequestDTO request) {
        String correcta = pregunta.getCorrectAnswer();
        if (correcta == null || correcta.isBlank()) {
            return false;
        }
        String enviada = request.getSelectedOptionId() != null
                ? request.getSelectedOptionId().toString()
                : request.getAnsweredText();
        return normalize(correcta).equals(normalize(enviada));
    }

    private String normalize(String valor) {
        return valor == null ? "" : valor.trim().toLowerCase();
    }

    private BigDecimal points(Question pregunta) {
        return pregunta.getPoints() != null ? pregunta.getPoints() : BigDecimal.ONE;
    }

    /** passing_score puede venir como fraccion (0.7) o porcentaje (70). */
    private BigDecimal passingScore(Test test) {
        BigDecimal valor = test.getPassingScore();
        if (valor == null) {
            return DEFAULT_PASSING_SCORE;
        }
        if (valor.compareTo(BigDecimal.ONE) <= 0) {
            return valor.multiply(new BigDecimal("100"));
        }
        return valor;
    }

    private StartAttemptResponseDTO toStartDto(UserTestAttempt intento) {
        StartAttemptResponseDTO dto = new StartAttemptResponseDTO();
        dto.setAttemptId(intento.getId());
        dto.setTestId(intento.getTest().getId());
        dto.setLivesRemaining(intento.getLivesRemaining());
        dto.setStartedAt(intento.getStartedAt());
        List<UUID> answered = new ArrayList<>();
        for (UserTestAnswer r : answerRepository.findByAttemptId(intento.getId())) {
            if (r.getQuestion() != null) {
                answered.add(r.getQuestion().getId());
            }
        }
        dto.setAnsweredQuestionIds(answered);
        return dto;
    }
}
