package com.sodablush.api.model;
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
@Table (name = "CODE_EXERCISES")
@Data 
public class CodeExercise {
    @Id 
    @Column (name = "id")
    private UUID id;

    @ManyToOne 
    @JoinColumn (name = "drink_id")
    private Drink drink;

    @Column (name = "instructions")
    private String instructions;

    @Column (name = "starter_code")
    private String starterCode;

    @Column (name = "expected_css")
    private String expectedCss;

    @Column (name = "output_html")
    private String outputHtml;

    @Column (name = "output_css")
    private String outputCss;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "validation_rules")
    private Map<String, Object> validationRules;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "hints")
    private Map<String, Object> hints;
}