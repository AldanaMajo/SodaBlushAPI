package com.sodablush.api.model;
import java.math.BigDecimal;
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
@Table(name = "QUESTIONS")
@Data 
public class Question {
    @Id 
    @Column (name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;
    
    @Column (name = "question_type")
    private String questionType;
    
    @Column (name = "prompt")
    private String prompt;

    @Column (name = "code_snippet")
    private String codeSnippet;

    @Column (name = "correct_answer")
    private String correctAnswer;

    @Column (name = "explanation")
    private String explanation;

    @Column (name = "order_index")
    private Integer orderIndex;

    @Column (name = "points")
    private BigDecimal points;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "options")
    private Map<String, Object>  options;
}