package com.sodablush.api.model;
import java.math.BigDecimal;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity 
@Table (name = "TESTS")
@Data 
public class Test {
    @Id 
    @Column(name = "id")    
    private UUID id;

   @ManyToOne
   @JoinColumn(name = "drink_id")
   private Drink drink;
   
    @Column(name = "test_type")
    private String testType;

    @Column(name = "passing_score")
    private BigDecimal passingScore;

    @Column(name = "max_attempts")
    private Integer maxAttempts;

    @Column(name = "lives_allowed")
    private Integer livesAllowed;

    @Column(name = "instructions")
    private String instructions;
}