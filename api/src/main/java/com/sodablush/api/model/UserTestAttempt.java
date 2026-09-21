package com.sodablush.api.model;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity    
@Table (name = "USER_TEST_ATTEMPTS")
@Data 
public class UserTestAttempt {
    @Id 
    @Column (name = "id")
    private UUID id;
    
    @ManyToOne 
    @JoinColumn (name = "user_id")
    private User user;

    @ManyToOne 
    @JoinColumn (name = "test_id")
    private Test test;    

    @ManyToOne 
    @JoinColumn (name = "drink_id")
    private Drink drink;

    @Column (name = "score")
    private Integer score;

    @Column (name = "passed")
    private Boolean passed;

    @Column (name = "lives_remaining")
    private Integer livesRemaining;

    @Column (name = "started_at")
    private LocalDateTime startedAt;

    @Column (name = "completed_at") 
    private LocalDateTime completedAt;
}   