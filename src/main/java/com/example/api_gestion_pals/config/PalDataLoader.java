package com.example.api_gestion_pals.config;

import com.example.api_gestion_pals.DAO.PalDAO;
import com.example.api_gestion_pals.model.*;
import com.example.api_gestion_pals.service.entity.Attack;
import com.example.api_gestion_pals.service.entity.Pal;
import com.example.api_gestion_pals.service.entity.Skill;
import com.example.api_gestion_pals.service.entity.Stat;
import com.example.api_gestion_pals.service.entity.Suitability;
import com.example.api_gestion_pals.service.entity.Speed;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@SuppressWarnings("unused")
public class PalDataLoader implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(PalDataLoader.class);

    private final PalDAO palDAO;
    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;

    public PalDataLoader(PalDAO palDAO, ObjectMapper objectMapper, EntityManager entityManager) {
        this.palDAO = palDAO;
        this.objectMapper = objectMapper;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        long count = palDAO.count();
        LOGGER.info("Nombre de pals dans la base : {}", count);
        if (count > 0) {
            LOGGER.info("La base contient déjà des données, import ignoré");
            return;
        }

        try (InputStream is = new ClassPathResource("pals.json").getInputStream()) {
            List<Pal> pals = objectMapper.readValue(is, new TypeReference<>() {
            });
            int batchSize = 50;
            for (int i = 0; i < pals.size(); i++) {
                Pal pal = pals.get(i);
                PalEntity entity = toEntity(pal);
                
                // Vérifier si l'entité existe déjà dans la session ou la base
                PalEntity existingEntity = entityManager.find(PalEntity.class, entity.getId());
                if (existingEntity == null) {
                    // Sauvegarder d'abord le PalEntity sans les relations en cascade
                    PalStatEntity stats = entity.getStats();
                    List<PalTypeEntity> types = new ArrayList<>(entity.getTypes());
                    List<PalSkillEntity> skills = new ArrayList<>(entity.getSkills());
                    List<PalSuitabilityEntity> suitabilities = new ArrayList<>(entity.getSuitabilities());
                    
                    // Retirer temporairement toutes les relations
                    entity.setStats(null);
                    entity.getTypes().clear();
                    entity.getSkills().clear();
                    entity.getSuitabilities().clear();
                    
                    // Persister le PalEntity seul
                    entityManager.persist(entity);
                    entityManager.flush();
                    
                    // Recharger l'entité gérée depuis la base
                    PalEntity managedEntity = entityManager.find(PalEntity.class, entity.getId());
                    
                    // Sauvegarder manuellement les relations
                    for (PalTypeEntity type : types) {
                        type.setPal(managedEntity);
                        entityManager.persist(type);
                        managedEntity.getTypes().add(type);
                    }
                    
                    for (PalSkillEntity skill : skills) {
                        skill.setPal(managedEntity);
                        entityManager.persist(skill);
                        managedEntity.getSkills().add(skill);
                    }
                    
                    for (PalSuitabilityEntity suitability : suitabilities) {
                        suitability.setPal(managedEntity);
                        entityManager.persist(suitability);
                        managedEntity.getSuitabilities().add(suitability);
                    }
                    
                    // Sauvegarder le PalStatEntity
                    if (stats != null) {
                        stats.setPal(managedEntity);
                        entityManager.persist(stats);
                        managedEntity.setStats(stats);
                    }
                }
                
                // Clear périodiquement pour éviter les problèmes de session
                if ((i + 1) % batchSize == 0) {
                    entityManager.clear();
                }
            }
            entityManager.clear();
            long finalCount = palDAO.count();
            LOGGER.info("Import de {} pals terminé. Nombre total de pals dans la base : {}", pals.size(), finalCount);
        } catch (IOException e) {
            LOGGER.error("Impossible de charger pals.json", e);
        }
    }

    private PalEntity toEntity(Pal pal) {
        PalEntity entity = new PalEntity();
        entity.setId(pal.id());
        entity.setPalKey(pal.key());
        entity.setName(pal.name());
        entity.setWiki(pal.wiki());
        entity.setRarity(pal.rarity());
        entity.setPrice(pal.price());
        entity.setSize(pal.size());

        // types
        if (pal.types() != null) {
            for (String t : pal.types()) {
                PalTypeEntity typeEntity = new PalTypeEntity();
                typeEntity.setType(t);
                typeEntity.setPal(entity);
                entity.getTypes().add(typeEntity);
            }
        }

        // suitabilities
        if (pal.suitability() != null) {
            for (Suitability s : pal.suitability()) {
                PalSuitabilityEntity se = new PalSuitabilityEntity();
                se.setType(s.type());
                se.setLevel(s.level());
                se.setPal(entity);
                entity.getSuitabilities().add(se);
            }
        }

        // skills
        if (pal.skills() != null) {
            for (Skill s : pal.skills()) {
                PalSkillEntity skillEntity = new PalSkillEntity();
                skillEntity.setLevel(s.level());
                skillEntity.setName(s.name());
                skillEntity.setType(s.type());
                skillEntity.setCooldown(s.cooldown());
                skillEntity.setPower(s.power());
                skillEntity.setDescription(s.description());
                skillEntity.setPal(entity);
                entity.getSkills().add(skillEntity);
            }
        }

        // stats
        if (pal.stats() != null) {
            PalStatEntity statEntity = getPalStatEntity(pal, entity);
            entity.setStats(statEntity);
        }

        return entity;
    }

    private static PalStatEntity getPalStatEntity(Pal pal, PalEntity entity) {
        Stat st = pal.stats();
        PalStatEntity statEntity = new PalStatEntity();
        statEntity.setHp(st.hp());
        Attack attack = st.attack();
        if (attack != null) {
            statEntity.setMeleeAttack(attack.melee());
            statEntity.setRangedAttack(attack.ranged());
        }
        statEntity.setDefense(st.defense());
        Speed speed = st.speed();
        if (speed != null) {
            statEntity.setRideSpeed(speed.ride());
            statEntity.setRunSpeed(speed.run());
            statEntity.setWalkSpeed(speed.walk());
        }
        statEntity.setStamina(st.stamina());
        statEntity.setSupport(st.support());
        statEntity.setFood(st.food());
        statEntity.setPal(entity);
        return statEntity;
    }
}


