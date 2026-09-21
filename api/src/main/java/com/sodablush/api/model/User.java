package com.sodablush.api.model;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "USERS")
@Data
public class User {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "email")
    private String email;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "auth0_id")
    private String auth0Id;

    @Column(name = "avatar_seed")
    private String avatarSeed;

    @Column(name = "locale")
    private String locale;

    @Column(name = "theme")
    private String theme;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sound_settings")
    private Map<String, Object> soundSettings;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "notification_settings")
    private Map<String, Object> notificationSettings;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}