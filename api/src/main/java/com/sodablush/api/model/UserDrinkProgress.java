package com.sodablush.api.model;
import java.math.BigDecimal;
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
@Table (name = "USER_DRINK_PROGRESS")  
@Data 
public class UserDrinkProgress {
    @Id 
    @Column (name = "id")
    private UUID id;

    @ManyToOne 
    @JoinColumn(name = "user_Id")
    private User user;
    
    @ManyToOne 
    @JoinColumn (name = "can_id")
    private Can can;

    @ManyToOne 
    @JoinColumn (name = "drink_id")
    private Drink drink;

    @Column (name = "status")
    private String status;

    @Column (name = "score")
    private BigDecimal score;

    @Column (name = "attempts")
    private Integer attempts;

    @Column (name = "completed_at")
    private LocalDateTime completedAt;
}
