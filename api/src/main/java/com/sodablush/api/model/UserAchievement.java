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
@Table (name = "USER_ACHIEVEMENTS")
@Data 
public class UserAchievement {
    @Id 
    @Column (name = "id")
    private UUID id;

    @ManyToOne 
    @JoinColumn (name = "user_id")
    private User user;

    @ManyToOne 
    @JoinColumn (name = "achievement_id") 
    private Achievement achievement;

    @Column (name = "progress")
    private BigDecimal progress;

    @Column (name = "unlocked_at")
    private LocalDateTime unlockedAt;

    @Column (name = "times_unlocked")
    private Integer timesUnlocked;
}