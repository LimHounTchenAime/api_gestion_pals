package com.example.api_gestion_pals.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pals")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PalEntity {

    @Id
    private Integer id;

    private String palKey;

    private String name;

    private String wiki;

    private Integer rarity;

    private Integer price;

    private String size;

    @OneToMany(mappedBy = "pal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PalTypeEntity> types = new ArrayList<>();

    @OneToMany(mappedBy = "pal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PalSkillEntity> skills = new ArrayList<>();

    @OneToMany(mappedBy = "pal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PalSuitabilityEntity> suitabilities = new ArrayList<>();

    @OneToOne(mappedBy = "pal", fetch = FetchType.LAZY)
    private PalStatEntity stats;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPalKey() {
        return palKey;
    }

    public void setPalKey(String palKey) {
        this.palKey = palKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWiki() {
        return wiki;
    }

    public void setWiki(String wiki) {
        this.wiki = wiki;
    }

    public Integer getRarity() {
        return rarity;
    }

    public void setRarity(Integer rarity) {
        this.rarity = rarity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<PalTypeEntity> getTypes() {
        return types;
    }

    public void setTypes(List<PalTypeEntity> types) {
        this.types = types;
    }

    public List<PalSkillEntity> getSkills() {
        return skills;
    }

    public void setSkills(List<PalSkillEntity> skills) {
        this.skills = skills;
    }

    public List<PalSuitabilityEntity> getSuitabilities() {
        return suitabilities;
    }

    public void setSuitabilities(List<PalSuitabilityEntity> suitabilities) {
        this.suitabilities = suitabilities;
    }

    public PalStatEntity getStats() {
        return stats;
    }

    public void setStats(PalStatEntity stats) {
        this.stats = stats;
    }
}


