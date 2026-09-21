package com.sodablush.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "USER_CAN_PROGRESS")
@Data
public class UserCanProgress {

    @Id 
    @Column(name = "id")
    private UUID id;

    @ManyToOne 
    @JoinColumn(name = "user_id") 
    private User user;

    @ManyToOne 
    @JoinColumn(name = "can_id")     
    private Can can;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "current_drink_id")
    private Drink currentDrink;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "times_completed")
    private Integer timesCompleted;
}