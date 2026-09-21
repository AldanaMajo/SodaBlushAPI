package com.sodablush.api.model;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity 
@Table(name = "USER_CODE_SUBMISSIONS")
@Data 
public class UserCodeSubmission {
    @Id 
    @Column(name = "id")
    private UUID id;
    
    @ManyToOne
    @JoinColumn (name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn (name = "drink_id")
    private Drink drink;

    @Column (name = "submitted_code")
    private String submittedCode;

    @Column (name = "is_correct")
    private Boolean isCorrect;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "validation_result")
    private Map<String, Object> validationResult;

    @Column (name = "output_snapshot")
    private String outputSnapshot;
    
    @Column (name = "created_at")
    private LocalDateTime createdAt;
}