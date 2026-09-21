package com.sodablush.api.model;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table (name = "ACHIEVEMENTS")
@Data 
public class Achievement {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column (name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column (name = "icon_url")
    private String iconUrl;

    @Column (name = "type")
    private String type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "criteria")
    private Map<String, Object>  criteria;

    @Column (name = "points")
    private Integer points;
}
