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
@Table (name = "PASSWORD_RESET_TOKENS")
@Data 
public class PasswordResetToken {
    @Id 
    @Column (name = "id")
    private UUID id;

    @ManyToOne 
    @JoinColumn (name = "user_id")
    private User user;

    @Column (name = "token")
    private String token;

    @Column (name = "expires_at")
    private LocalDateTime expiresAt;
}