package com.example.api_gestion_pals.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "pal_suitabilities")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PalSuitabilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private Integer level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pal_id")
    @JsonIgnore
    private PalEntity pal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public PalEntity getPal() {
        return pal;
    }

    public void setPal(PalEntity pal) {
        this.pal = pal;
    }
}


