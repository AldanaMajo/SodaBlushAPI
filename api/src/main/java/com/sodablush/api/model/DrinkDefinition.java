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
@Table (name = "DRINK_DEFINITIONS")
@Data 
public class DrinkDefinition {
    @Id
    @Column(name = "drink_id")
    private UUID id;

    @OneToOne
    @MapsId 
    @JoinColumn(name = "drink_id")
    private Drink drink;

    @Column (name = "definition_text", columnDefinition = "text")
    private String definitionText;

    @Column (name = "short_text", columnDefinition = "text")
    private String shortText;

    @Column (name = "animated_gif_url", columnDefinition = "text")
    private String animatedGifUrl;

    @Column (name = "example_code", columnDefinition = "text")
    private String exampleCode;

    @Column (name = "output_html", columnDefinition = "text")
    private String outputHtml;

    @Column (name = "output_css", columnDefinition = "text")
    private String outputCss;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tips")
    private Map<String, Object> tips;
}