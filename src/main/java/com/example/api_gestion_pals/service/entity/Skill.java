package com.example.api_gestion_pals.service.entity;

public record Skill(
        int level,
        String name,
        String type,
        int cooldown,
        int power,
        String description
) {
}
