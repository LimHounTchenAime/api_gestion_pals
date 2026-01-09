package com.example.api_gestion_pals.controller;

import com.example.api_gestion_pals.model.*;
import com.example.api_gestion_pals.service.PalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PalController.class)
class PalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PalService palService;

    @Autowired
    private ObjectMapper objectMapper;

    private PalEntity testPal;

    @BeforeEach
    void setUp() {
        testPal = createTestPal(1, "TestPal");
    }

    @Test
    void shouldGetPalById() throws Exception {
        when(palService.getById(1)).thenReturn(Optional.of(testPal));

        mockMvc.perform(get("/pals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TestPal"));

        verify(palService).getById(1);
    }

    @Test
    void shouldReturn404WhenPalNotFound() throws Exception {
        when(palService.getById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/pals/999"))
                .andExpect(status().isNotFound());

        verify(palService).getById(999);
    }

    @Test
    void shouldGetPalByName() throws Exception {
        when(palService.getByName("TestPal")).thenReturn(Optional.of(testPal));

        mockMvc.perform(get("/pals/by-name").param("name", "TestPal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestPal"));

        verify(palService).getByName("TestPal");
    }

    @Test
    void shouldGetPalsByType() throws Exception {
        List<PalEntity> pals = List.of(testPal);
        when(palService.getByType("fire")).thenReturn(pals);

        mockMvc.perform(get("/pals/by-type").param("type", "fire"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("TestPal"));

        verify(palService).getByType("fire");
    }

    @Test
    void shouldGetAllPals() throws Exception {
        List<PalEntity> pals = List.of(testPal, createTestPal(2, "Pal2"));
        when(palService.getAll()).thenReturn(pals);

        mockMvc.perform(get("/pals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());

        verify(palService).getAll();
    }

    @Test
    void shouldCreatePal() throws Exception {
        PalEntity newPal = createTestPal(138, "NewPal");
        when(palService.save(any(PalEntity.class))).thenReturn(newPal);

        mockMvc.perform(post("/pals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPal)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(138))
                .andExpect(jsonPath("$.name").value("NewPal"));

        verify(palService).save(any(PalEntity.class));
    }

    @Test
    void shouldUpdatePal() throws Exception {
        PalEntity updatedPal = createTestPal(1, "UpdatedPal");
        when(palService.getById(1)).thenReturn(Optional.of(testPal));
        when(palService.save(any(PalEntity.class))).thenReturn(updatedPal);

        mockMvc.perform(put("/pals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedPal"));

        verify(palService).getById(1);
        verify(palService).save(any(PalEntity.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentPal() throws Exception {
        PalEntity updatedPal = createTestPal(999, "UpdatedPal");
        when(palService.getById(999)).thenReturn(Optional.empty());

        mockMvc.perform(put("/pals/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPal)))
                .andExpect(status().isNotFound());

        verify(palService).getById(999);
        verify(palService, never()).save(any());
    }

    @Test
    void shouldDeletePal() throws Exception {
        doNothing().when(palService).deleteById(1);

        mockMvc.perform(delete("/pals/1"))
                .andExpect(status().isNoContent());

        verify(palService).deleteById(1);
    }

    @Test
    void shouldGetSkills() throws Exception {
        PalSkillEntity skill = createTestSkill(1L, "Fireball");
        List<PalSkillEntity> skills = List.of(skill);
        when(palService.getSkills(1)).thenReturn(skills);

        mockMvc.perform(get("/pals/1/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Fireball"));

        verify(palService).getSkills(1);
    }

    @Test
    void shouldAddSkill() throws Exception {
        PalSkillEntity newSkill = createTestSkill(null, "NewSkill");
        when(palService.addSkill(eq(1), any(PalSkillEntity.class))).thenReturn(newSkill);

        mockMvc.perform(post("/pals/1/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSkill)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("NewSkill"));

        verify(palService).addSkill(eq(1), any(PalSkillEntity.class));
    }

    @Test
    void shouldGetTypes() throws Exception {
        PalTypeEntity type = createTestType(1L, "fire");
        List<PalTypeEntity> types = List.of(type);
        when(palService.getTypes(1)).thenReturn(types);

        mockMvc.perform(get("/pals/1/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].type").value("fire"));

        verify(palService).getTypes(1);
    }

    @Test
    void shouldAddType() throws Exception {
        PalTypeEntity newType = createTestType(null, "water");
        when(palService.addType(eq(1), anyString())).thenReturn(newType);

        mockMvc.perform(post("/pals/1/types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"water\""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.type").value("water"));

        verify(palService).addType(eq(1), anyString());
    }

    @Test
    void shouldRemoveType() throws Exception {
        doNothing().when(palService).removeType(1, 1L);

        mockMvc.perform(delete("/pals/1/types/1"))
                .andExpect(status().isNoContent());

        verify(palService).removeType(1, 1L);
    }

    @Test
    void shouldGetSortedByRarity() throws Exception {
        PalEntity pal1 = createTestPal(1, "CommonPal");
        pal1.setRarity(1);
        PalEntity pal2 = createTestPal(2, "RarePal");
        pal2.setRarity(5);
        List<PalEntity> sortedPals = List.of(pal2, pal1);
        when(palService.getSortedByRarity(5)).thenReturn(sortedPals);

        mockMvc.perform(get("/pals/sorted/rarity").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].rarity").value(5));

        verify(palService).getSortedByRarity(5);
    }

    @Test
    void shouldGetSortedByPrice() throws Exception {
        PalEntity pal1 = createTestPal(1, "CheapPal");
        pal1.setPrice(100);
        PalEntity pal2 = createTestPal(2, "ExpensivePal");
        pal2.setPrice(1000);
        List<PalEntity> sortedPals = List.of(pal2, pal1);
        when(palService.getSortedByPrice(5)).thenReturn(sortedPals);

        mockMvc.perform(get("/pals/sorted/price").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].price").value(1000));

        verify(palService).getSortedByPrice(5);
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
}
