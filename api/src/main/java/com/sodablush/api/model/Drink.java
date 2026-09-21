package com.sodablush.api.model;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "DRINKS")
@Data
public class Drink {
    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "can_id")
    private Can can;

    @Column(name = "step_order")
    private Integer stepOrder;

    @Column(name = "type")
    private String type;

    @Column(name = "title")
    private String title;   
}