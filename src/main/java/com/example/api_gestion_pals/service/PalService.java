package com.example.api_gestion_pals.service;

import com.example.api_gestion_pals.DAO.PalDAO;
import com.example.api_gestion_pals.model.PalEntity;
import com.example.api_gestion_pals.model.PalSkillEntity;
import com.example.api_gestion_pals.model.PalStatEntity;
import com.example.api_gestion_pals.model.PalSuitabilityEntity;
import com.example.api_gestion_pals.model.PalTypeEntity;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PalService {

    private final PalDAO palDAO;
    private final EntityManager entityManager;

    public PalService(PalDAO palDAO, EntityManager entityManager) {
        this.palDAO = palDAO;
        this.entityManager = entityManager;
    }

    public Optional<PalEntity> getById(int id) {
        return palDAO.findById(id);
    }

    public Optional<PalEntity> getByName(String name) {
        return palDAO.findByNameIgnoreCase(name);
    }

    public List<PalEntity> getByType(String type) {
        return palDAO.findByTypes_TypeIgnoreCase(type);
    }

    public List<PalEntity> getAll() {
        return palDAO.findAll();
    }

    public PalEntity save(PalEntity pal) {
        // Vérifier si c'est une création ou une mise à jour
        boolean isNew = pal.getId() == null || !palDAO.existsById(pal.getId());
        
        if (isNew) {
            // Pour une nouvelle entité, gérer manuellement la persistance comme dans PalDataLoader
            PalStatEntity stats = pal.getStats();
            
            // Retirer temporairement les stats
            pal.setStats(null);
            
            // Réinitialiser les IDs des entités enfants
            if (pal.getTypes() != null) {
                pal.getTypes().forEach(type -> {
                    type.setId(null);
                    type.setPal(pal);
                });
            }
            if (pal.getSkills() != null) {
                pal.getSkills().forEach(skill -> {
                    skill.setId(null);
                    skill.setPal(pal);
                });
            }
            if (pal.getSuitabilities() != null) {
                pal.getSuitabilities().forEach(suitability -> {
                    suitability.setId(null);
                    suitability.setPal(pal);
                });
            }
            
            // Sauvegarder le PalEntity (les relations @OneToMany seront sauvegardées en cascade)
            PalEntity savedPal = palDAO.save(pal);
            entityManager.flush();
            
            // Recharger l'entité gérée depuis la base
            PalEntity managedPal = entityManager.find(PalEntity.class, savedPal.getId());
            
            // Sauvegarder manuellement le PalStatEntity
            if (stats != null) {
                stats.setId(null);
                stats.setPal(managedPal);
                entityManager.persist(stats);
                managedPal.setStats(stats);
            }
            
            return managedPal;
        } else {
            // Pour une mise à jour, charger l'entité existante et mettre à jour
            PalEntity existing = palDAO.findById(pal.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Pal non trouvé : " + pal.getId()));
            
            // Mettre à jour les champs de base
            existing.setPalKey(pal.getPalKey());
            existing.setName(pal.getName());
            existing.setWiki(pal.getWiki());
            existing.setRarity(pal.getRarity());
            existing.setPrice(pal.getPrice());
            existing.setSize(pal.getSize());
            
            // Mettre à jour les types (remplacer la liste)
            if (pal.getTypes() != null) {
                existing.getTypes().clear();
                pal.getTypes().forEach(type -> {
                    type.setId(null);
                    type.setPal(existing);
                    existing.getTypes().add(type);
                });
            }
            
            // Mettre à jour les skills (remplacer la liste)
            if (pal.getSkills() != null) {
                existing.getSkills().clear();
                pal.getSkills().forEach(skill -> {
                    skill.setId(null);
                    skill.setPal(existing);
                    existing.getSkills().add(skill);
                });
            }
            
            // Mettre à jour les suitabilities (remplacer la liste)
            if (pal.getSuitabilities() != null) {
                existing.getSuitabilities().clear();
                pal.getSuitabilities().forEach(suitability -> {
                    suitability.setId(null);
                    suitability.setPal(existing);
                    existing.getSuitabilities().add(suitability);
                });
            }
            
            // Mettre à jour les stats
            if (pal.getStats() != null) {
                if (existing.getStats() != null) {
                    // Mettre à jour les stats existantes
                    PalStatEntity stats = existing.getStats();
                    stats.setHp(pal.getStats().getHp());
                    stats.setMeleeAttack(pal.getStats().getMeleeAttack());
                    stats.setRangedAttack(pal.getStats().getRangedAttack());
                    stats.setDefense(pal.getStats().getDefense());
                    stats.setRideSpeed(pal.getStats().getRideSpeed());
                    stats.setRunSpeed(pal.getStats().getRunSpeed());
                    stats.setWalkSpeed(pal.getStats().getWalkSpeed());
                    stats.setStamina(pal.getStats().getStamina());
                    stats.setSupport(pal.getStats().getSupport());
                    stats.setFood(pal.getStats().getFood());
                } else {
                    // Créer de nouvelles stats - gérer manuellement la persistance
                    PalStatEntity newStats = pal.getStats();
                    newStats.setId(null);
                    newStats.setPal(existing);
                    entityManager.persist(newStats);
                    existing.setStats(newStats);
                }
            }
            
            PalEntity saved = palDAO.save(existing);
            entityManager.flush();
            return saved;
        }

    }

    public void deleteById(int id) {
        palDAO.deleteById(id);
    }

    public List<PalSkillEntity> getSkills(int palId) {
        PalEntity pal = palDAO.findById(palId)
                .orElseThrow(() -> new IllegalArgumentException("Pal non trouvé : " + palId));
        return pal.getSkills();
    }

    public PalSkillEntity addSkill(int palId, PalSkillEntity skill) {
        PalEntity pal = palDAO.findById(palId)
                .orElseThrow(() -> new IllegalArgumentException("Pal non trouvé : " + palId));
        skill.setPal(pal);
        pal.getSkills().add(skill);
        palDAO.save(pal);
        return skill;
    }

    public PalSkillEntity modifySkill(int palId, long skillId, PalSkillEntity updated) {
        PalEntity pal = palDAO.findById(palId)
                .orElseThrow(() -> new IllegalArgumentException("Pal non trouvé : " + palId));
        PalSkillEntity existing = pal.getSkills().stream()
                .filter(s -> s.getId() == skillId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Skill non trouvé : " + skillId));

        existing.setName(updated.getName());
        existing.setType(updated.getType());
        existing.setLevel(updated.getLevel());
        existing.setCooldown(updated.getCooldown());
        existing.setPower(updated.getPower());
        existing.setDescription(updated.getDescription());

        return existing;
    }

    public List<PalTypeEntity> getTypes(int palId) {
        PalEntity pal = palDAO.findById(palId)
                .orElseThrow(() -> new IllegalArgumentException("Pal non trouvé : " + palId));
        Hibernate.initialize(pal.getTypes());
        return pal.getTypes();
    }

    public PalTypeEntity addType(int palId, String type) {
        PalEntity pal = palDAO.findById(palId)
                .orElseThrow(() -> new IllegalArgumentException("Pal non trouvé : " + palId));
        PalTypeEntity palType = new PalTypeEntity();
        palType.setType(type);
        palType.setPal(pal);
        pal.getTypes().add(palType);
        palDAO.save(pal);
        return palType;
    }

    public void removeType(int palId, long typeId) {
        PalEntity pal = palDAO.findById(palId)
                .orElseThrow(() -> new IllegalArgumentException("Pal non trouvé : " + palId));
        boolean removed = pal.getTypes().removeIf(t -> t.getId() == typeId);
        if (!removed) {
            throw new IllegalArgumentException("Type non trouvé : " + typeId);
        }
        palDAO.save(pal);
    }

    public List<PalEntity> getSortedByRarity(int limit) {
        return palDAO.findAll().stream()
                .sorted(Comparator.comparing(PalEntity::getRarity).reversed())
                .limit(limit)
                .toList();
    }

    public List<PalEntity> getSortedByPrice(int limit) {
        return palDAO.findAll().stream()
                .sorted(Comparator.comparing(PalEntity::getPrice).reversed())
                .limit(limit)
                .toList();
    }
}
