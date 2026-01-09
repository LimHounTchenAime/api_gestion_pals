package com.example.api_gestion_pals.service;

import com.example.api_gestion_pals.DAO.PalDAO;
import com.example.api_gestion_pals.model.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PalServiceTest {

    @Mock
    private PalDAO palDAO;

    @Mock
    private EntityManager entityManager;

    private PalService palService;

    @BeforeEach
    void setUp() {
        palService = new PalService(palDAO, entityManager);
    }

    @Test
    void shouldGetById() {
        PalEntity pal = createTestPal(1, "TestPal");
        when(palDAO.findById(1)).thenReturn(Optional.of(pal));

        Optional<PalEntity> result = palService.getById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("TestPal");
        verify(palDAO).findById(1);
    }

    @Test
    void shouldReturnEmptyWhenPalNotFound() {
        when(palDAO.findById(999)).thenReturn(Optional.empty());

        Optional<PalEntity> result = palService.getById(999);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldGetByName() {
        PalEntity pal = createTestPal(1, "TestPal");
        when(palDAO.findByNameIgnoreCase("TestPal")).thenReturn(Optional.of(pal));

        Optional<PalEntity> result = palService.getByName("TestPal");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("TestPal");
        verify(palDAO).findByNameIgnoreCase("TestPal");
    }

    @Test
    void shouldGetByType() {
        List<PalEntity> pals = List.of(createTestPal(1, "FirePal"));
        when(palDAO.findByTypes_TypeIgnoreCase("fire")).thenReturn(pals);

        List<PalEntity> result = palService.getByType("fire");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("FirePal");
        verify(palDAO).findByTypes_TypeIgnoreCase("fire");
    }

    @Test
    void shouldGetAll() {
        List<PalEntity> pals = List.of(
                createTestPal(1, "Pal1"),
                createTestPal(2, "Pal2")
        );
        when(palDAO.findAll()).thenReturn(pals);

        List<PalEntity> result = palService.getAll();

        assertThat(result).hasSize(2);
        verify(palDAO).findAll();
    }

    @Test
    void shouldSaveNewPal() {
        PalEntity pal = createTestPalWithRelations(138, "NewPal");
        when(palDAO.existsById(138)).thenReturn(false);
        when(palDAO.save(any(PalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(entityManager.find(PalEntity.class, 138)).thenReturn(pal);

        PalEntity saved = palService.save(pal);

        assertThat(saved).isNotNull();
        verify(palDAO).existsById(138);
        verify(palDAO).save(any(PalEntity.class));
        verify(entityManager).flush();
    }

    @Test
    void shouldUpdateExistingPal() {
        PalEntity existing = createTestPal(1, "ExistingPal");
        PalEntity updated = createTestPal(1, "UpdatedPal");
        when(palDAO.existsById(1)).thenReturn(true);
        when(palDAO.findById(1)).thenReturn(Optional.of(existing));
        when(palDAO.save(any(PalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PalEntity result = palService.save(updated);

        assertThat(result).isNotNull();
        verify(palDAO).existsById(1);
        verify(palDAO).findById(1);
        verify(palDAO).save(any(PalEntity.class));
    }

    @Test
    void shouldDeleteById() {
        doNothing().when(palDAO).deleteById(1);

        palService.deleteById(1);

        verify(palDAO).deleteById(1);
    }

    @Test
    void shouldGetSkills() {
        PalEntity pal = createTestPal(1, "TestPal");
        PalSkillEntity skill = createTestSkill(1L, "Fireball");
        pal.getSkills().add(skill);
        when(palDAO.findById(1)).thenReturn(Optional.of(pal));

        List<PalSkillEntity> result = palService.getSkills(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Fireball");
        verify(palDAO).findById(1);
    }

    @Test
    void shouldThrowExceptionWhenPalNotFoundForSkills() {
        when(palDAO.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> palService.getSkills(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pal non trouvé : 999");
    }

    @Test
    void shouldAddSkill() {
        PalEntity pal = createTestPal(1, "TestPal");
        PalSkillEntity newSkill = createTestSkill(null, "NewSkill");
        when(palDAO.findById(1)).thenReturn(Optional.of(pal));
        when(palDAO.save(any(PalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PalSkillEntity result = palService.addSkill(1, newSkill);

        assertThat(result).isNotNull();
        assertThat(pal.getSkills()).contains(newSkill);
        verify(palDAO).findById(1);
        verify(palDAO).save(pal);
    }

    @Test
    void shouldGetTypes() {
        PalEntity pal = createTestPal(1, "TestPal");
        PalTypeEntity type = createTestType(1L, "fire");
        pal.getTypes().add(type);
        when(palDAO.findById(1)).thenReturn(Optional.of(pal));

        List<PalTypeEntity> result = palService.getTypes(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo("fire");
        verify(palDAO).findById(1);
    }

    @Test
    void shouldAddType() {
        PalEntity pal = createTestPal(1, "TestPal");
        when(palDAO.findById(1)).thenReturn(Optional.of(pal));
        when(palDAO.save(any(PalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PalTypeEntity result = palService.addType(1, "water");

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("water");
        assertThat(pal.getTypes()).contains(result);
        verify(palDAO).findById(1);
        verify(palDAO).save(pal);
    }

    @Test
    void shouldRemoveType() {
        PalEntity pal = createTestPal(1, "TestPal");
        PalTypeEntity type = createTestType(1L, "fire");
        pal.getTypes().add(type);
        when(palDAO.findById(1)).thenReturn(Optional.of(pal));

        palService.removeType(1, 1L);

        assertThat(pal.getTypes()).isEmpty();
        verify(palDAO).findById(1);
    }

    @Test
    void shouldGetSortedByRarity() {
        PalEntity pal1 = createTestPal(1, "CommonPal");
        pal1.setRarity(1);
        PalEntity pal2 = createTestPal(2, "RarePal");
        pal2.setRarity(5);
        List<PalEntity> allPals = List.of(pal1, pal2);
        when(palDAO.findAll()).thenReturn(allPals);

        List<PalEntity> result = palService.getSortedByRarity(2);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRarity()).isGreaterThan(result.get(1).getRarity());
        verify(palDAO).findAll();
    }

    @Test
    void shouldGetSortedByPrice() {
        PalEntity pal1 = createTestPal(1, "CheapPal");
        pal1.setPrice(100);
        PalEntity pal2 = createTestPal(2, "ExpensivePal");
        pal2.setPrice(1000);
        List<PalEntity> allPals = List.of(pal1, pal2);
        when(palDAO.findAll()).thenReturn(allPals);

        List<PalEntity> result = palService.getSortedByPrice(2);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPrice()).isGreaterThan(result.get(1).getPrice());
        verify(palDAO).findAll();
    }

    // Méthodes utilitaires pour créer des entités de test
    private PalEntity createTestPal(Integer id, String name) {
        PalEntity pal = new PalEntity();
        pal.setId(id);
        pal.setName(name);
        pal.setPalKey(String.valueOf(id));
        pal.setRarity(1);
        pal.setPrice(100);
        pal.setTypes(new ArrayList<>());
        pal.setSkills(new ArrayList<>());
        pal.setSuitabilities(new ArrayList<>());
        return pal;
    }

    private PalEntity createTestPalWithRelations(Integer id, String name) {
        PalEntity pal = createTestPal(id, name);
        PalTypeEntity type = createTestType(null, "neutral");
        pal.getTypes().add(type);
        PalSkillEntity skill = createTestSkill(null, "test_skill");
        pal.getSkills().add(skill);
        PalStatEntity stats = createTestStats();
        pal.setStats(stats);
        return pal;
    }

    private PalSkillEntity createTestSkill(Long id, String name) {
        PalSkillEntity skill = new PalSkillEntity();
        skill.setId(id);
        skill.setName(name);
        skill.setLevel(1);
        skill.setType("neutral");
        skill.setCooldown(1);
        skill.setPower(10);
        skill.setDescription("Test skill");
        return skill;
    }

    private PalTypeEntity createTestType(Long id, String type) {
        PalTypeEntity typeEntity = new PalTypeEntity();
        typeEntity.setId(id);
        typeEntity.setType(type);
        return typeEntity;
    }

    private PalStatEntity createTestStats() {
        PalStatEntity stats = new PalStatEntity();
        stats.setHp(100);
        stats.setMeleeAttack(50);
        stats.setRangedAttack(50);
        stats.setDefense(50);
        stats.setRideSpeed(500);
        stats.setRunSpeed(400);
        stats.setWalkSpeed(40);
        stats.setStamina(100);
        stats.setSupport(100);
        stats.setFood(2);
        return stats;
    }
}
