package com.sodablush.api.model;
import java.util.UUID;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "FEEDBACK")
@Data 
public class Feedback {
    @Id
    @Column (name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "type")
    private String type;

    @Column (name = "message")
    private String message;

    @Column(name = "screenshot_url")
    private String screenshotUrl;

    @Column (name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}