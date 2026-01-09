package com.example.api_gestion_pals.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "pal_stats")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PalStatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer hp;

    private Integer meleeAttack;

    private Integer rangedAttack;

    private Integer defense;

    private Integer rideSpeed;

    private Integer runSpeed;

    private Integer walkSpeed;

    private Integer stamina;

    private Integer support;

    private Integer food;

    @OneToOne
    @JoinColumn(name = "pal_id", unique = true)
    @JsonIgnore
    private PalEntity pal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getHp() {
        return hp;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public Integer getMeleeAttack() {
        return meleeAttack;
    }

    public void setMeleeAttack(Integer meleeAttack) {
        this.meleeAttack = meleeAttack;
    }

    public Integer getRangedAttack() {
        return rangedAttack;
    }

    public void setRangedAttack(Integer rangedAttack) {
        this.rangedAttack = rangedAttack;
    }

    public Integer getDefense() {
        return defense;
    }

    public void setDefense(Integer defense) {
        this.defense = defense;
    }

    public Integer getRideSpeed() {
        return rideSpeed;
    }

    public void setRideSpeed(Integer rideSpeed) {
        this.rideSpeed = rideSpeed;
    }

    public Integer getRunSpeed() {
        return runSpeed;
    }

    public void setRunSpeed(Integer runSpeed) {
        this.runSpeed = runSpeed;
    }

    public Integer getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(Integer walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    public Integer getStamina() {
        return stamina;
    }

    public void setStamina(Integer stamina) {
        this.stamina = stamina;
    }

    public Integer getSupport() {
        return support;
    }

    public void setSupport(Integer support) {
        this.support = support;
    }

    public Integer getFood() {
        return food;
    }

    public void setFood(Integer food) {
        this.food = food;
    }

    public PalEntity getPal() {
        return pal;
    }

    public void setPal(PalEntity pal) {
        this.pal = pal;
    }
}


