package com.sodablush.api.model;

import java.util.Map;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity 
@Table (name = "DRINK_SYNTAXES")
@Data
public class DrinkSyntax {
    @Id
    @Column(name = "drink_id")
    private UUID id;

    @OneToOne
    @MapsId 
    @JoinColumn(name = "drink_id")
    private Drink drink;

    @Column(name = "syntax_template", columnDefinition = "text")
    private String syntaxTemplate;

    @Column(name = "explanation", columnDefinition = "text")
    private String explanation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recommendations")
    private Map<String, Object> recommendations;

    @Column(name = "output_demo_html", columnDefinition = "text")
    private String outputDemoHtml;

    @Column(name = "output_demo_css", columnDefinition = "text")
    private String outputDemoCss;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tokens")
    private Map<String, Object> tokens;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "animations")
    private Map<String, Object> animations;
}