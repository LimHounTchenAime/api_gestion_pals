package com.example.api_gestion_pals.controller;

import com.example.api_gestion_pals.DAO.PalDAO;
import com.example.api_gestion_pals.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PalControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PalDAO palDAO;

    @Autowired
    private ObjectMapper objectMapper;

    private PalEntity testPal;

    @BeforeEach
    void setUp() {
        palDAO.deleteAll();
        testPal = createAndSaveTestPal(1, "TestPal");
    }

    @Test
    void shouldGetPalById() throws Exception {
        mockMvc.perform(get("/pals/{id}", testPal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testPal.getId()))
                .andExpect(jsonPath("$.name").value("TestPal"));
    }

    @Test
    void shouldReturn404WhenPalNotFound() throws Exception {
        mockMvc.perform(get("/pals/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetPalByName() throws Exception {
        mockMvc.perform(get("/pals/by-name").param("name", "TestPal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestPal"));
    }

    @Test
    void shouldGetPalsByType() throws Exception {
        // Créer un pal avec le type fire
        PalEntity firePal = createAndSaveTestPal(2, "FirePal");
        PalTypeEntity fireType = new PalTypeEntity();
        fireType.setType("fire");
        fireType.setPal(firePal);
        firePal.getTypes().add(fireType);
        palDAO.save(firePal);

        mockMvc.perform(get("/pals/by-type").param("type", "fire"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("FirePal"));
    }

    @Test
    void shouldGetAllPals() throws Exception {
        createAndSaveTestPal(2, "Pal2");

        mockMvc.perform(get("/pals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldCreatePal() throws Exception {
        PalEntity newPal = createTestPal(138, "NewPal");
        newPal.setStats(createTestStats());

        mockMvc.perform(post("/pals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPal)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(138))
                .andExpect(jsonPath("$.name").value("NewPal"));

        assertThat(palDAO.findById(138)).isPresent();
    }

    @Test
    void shouldUpdatePal() throws Exception {
        PalEntity updatedPal = createTestPal(testPal.getId(), "UpdatedPal");
        updatedPal.setRarity(5);
        updatedPal.setPrice(500);

        mockMvc.perform(put("/pals/{id}", testPal.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedPal"))
                .andExpect(jsonPath("$.rarity").value(5));

        PalEntity saved = palDAO.findById(testPal.getId()).orElseThrow();
        assertThat(saved.getName()).isEqualTo("UpdatedPal");
        assertThat(saved.getRarity()).isEqualTo(5);
    }

    @Test
    void shouldDeletePal() throws Exception {
        mockMvc.perform(delete("/pals/{id}", testPal.getId()))
                .andExpect(status().isNoContent());

        assertThat(palDAO.findById(testPal.getId())).isEmpty();
    }

    @Test
    void shouldGetSkills() throws Exception {
        PalSkillEntity skill = createTestSkill(null, "Fireball");
        skill.setPal(testPal);
        testPal.getSkills().add(skill);
        palDAO.save(testPal);

        mockMvc.perform(get("/pals/{id}/skills", testPal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Fireball"));
    }

    @Test
    void shouldAddSkill() throws Exception {
        PalSkillEntity newSkill = createTestSkill(null, "NewSkill");

        mockMvc.perform(post("/pals/{id}/skills", testPal.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSkill)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("NewSkill"));

        PalEntity saved = palDAO.findById(testPal.getId()).orElseThrow();
        assertThat(saved.getSkills()).hasSize(1);
        assertThat(saved.getSkills().get(0).getName()).isEqualTo("NewSkill");
    }

    @Test
    void shouldGetTypes() throws Exception {
        PalTypeEntity type = createTestType(null, "fire");
        type.setPal(testPal);
        testPal.getTypes().add(type);
        palDAO.save(testPal);

        mockMvc.perform(get("/pals/{id}/types", testPal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].type").value("fire"));
    }

    @Test
    void shouldAddType() throws Exception {
        mockMvc.perform(post("/pals/{id}/types", testPal.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"water\""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("water"));

        PalEntity saved = palDAO.findById(testPal.getId()).orElseThrow();
        assertThat(saved.getTypes()).hasSize(1);
        assertThat(saved.getTypes().get(0).getType()).isEqualTo("water");
    }

    @Test
    void shouldRemoveType() throws Exception {
        PalTypeEntity type = createTestType(null, "fire");
        type.setPal(testPal);
        testPal.getTypes().add(type);
        palDAO.save(testPal);
        Long typeId = testPal.getTypes().get(0).getId();

        mockMvc.perform(delete("/pals/{id}/types/{typeId}", testPal.getId(), typeId))
                .andExpect(status().isNoContent());

        PalEntity saved = palDAO.findById(testPal.getId()).orElseThrow();
        assertThat(saved.getTypes()).isEmpty();
    }

    @Test
    void shouldGetSortedByRarity() throws Exception {
        PalEntity commonPal = createAndSaveTestPal(2, "CommonPal");
        commonPal.setRarity(1);
        PalEntity rarePal = createAndSaveTestPal(3, "RarePal");
        rarePal.setRarity(5);
        palDAO.save(commonPal);
        palDAO.save(rarePal);

        mockMvc.perform(get("/pals/sorted/rarity").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].rarity").value(5));
    }

    @Test
    void shouldGetSortedByPrice() throws Exception {
        PalEntity cheapPal = createAndSaveTestPal(2, "CheapPal");
        cheapPal.setPrice(100);
        PalEntity expensivePal = createAndSaveTestPal(3, "ExpensivePal");
        expensivePal.setPrice(1000);
        palDAO.save(cheapPal);
        palDAO.save(expensivePal);

        mockMvc.perform(get("/pals/sorted/price").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].price").value(1000));
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

    private PalEntity createAndSaveTestPal(Integer id, String name) {
        PalEntity pal = createTestPal(id, name);
        return palDAO.save(pal);
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