package com.example.api_gestion_pals.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "pal_types")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PalTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

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

    public PalEntity getPal() {
        return pal;
    }

    public void setPal(PalEntity pal) {
        this.pal = pal;
    }
}


