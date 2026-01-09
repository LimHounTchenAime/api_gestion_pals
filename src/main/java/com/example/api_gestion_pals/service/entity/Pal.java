package com.example.api_gestion_pals.service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Pal(
    @JsonProperty("_id") int id,
    String key,
    String name,
    String wiki,
    List<String> types,
    List<Suitability> suitability,
    List<String> drops,
    List<Skill> skills,
    Stat stats,
    int rarity,
    int price,
    String size
) {
}
