package com.sodablush.api.model;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity 
@Table (name = "CSS_CONCEPTS")
@Data 
public class CssConcept {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column (name = "name")
    private String name;

    @Column (name = "slug")
    private String slug;

    @Column (name = "description")
    private String description;

    @Column (name = "mdn_url")
    private String mdnUrl;

    @Column (name = "order_index")
    private Integer orderIndex;
}