package com.sodablush.api.model;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "CANS")
@Data
public class Can {
    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "concept_id")
    private CssConcept concept;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "unlock_order")
    private Integer unlockOrder;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "full_image_url")
    private String fullImageUrl;

    @Column(name = "empty_image_url")
    private String emptyImageUrl;

    @Column(name = "open_sound_url")
    private String openSoundUrl;

    @Column(name = "crush_sound_url")
    private String crushSoundUrl;
}