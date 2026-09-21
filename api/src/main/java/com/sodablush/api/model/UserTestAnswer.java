package com.sodablush.api.model;
import java.util.UUID;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity 
@Table (name = "USER_TEST_ANSWERS")
@Data 
public class UserTestAnswer {
    @Id 
    @Column (name = "id")   
    private UUID id;

    @ManyToOne 
    @JoinColumn (name = "attempt_id")
    private UserTestAttempt attempt;

    @ManyToOne
    @JoinColumn (name = "question_id")
    private Question question;

    @Column (name = "selected_option_id")
    private UUID selectedOptionId;

    @Column (name = "answered_text")
    private String answeredText;

    @Column (name = "is_correct")
    private Boolean isCorrect;

    @Column (name = "time_spent_seconds")
    private Integer timeSpentSeconds;
}