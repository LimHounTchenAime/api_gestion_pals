package com.example.api_gestion_pals.service.entity;

public record Stat(
        int hp,
        Attack attack,
        int defense,
        Speed speed,
        int stamina,
        int support,
        int food
) {
}
