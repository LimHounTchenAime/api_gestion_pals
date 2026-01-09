package com.example.api_gestion_pals.controller;

import com.example.api_gestion_pals.model.PalEntity;
import com.example.api_gestion_pals.model.PalSkillEntity;
import com.example.api_gestion_pals.model.PalTypeEntity;
import com.example.api_gestion_pals.service.PalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pals")
@SuppressWarnings("unused")
public class PalController {

    private final PalService palService;

    public PalController(PalService palService) {
        this.palService = palService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PalEntity> getById(@PathVariable int id) {
        return palService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-name")
    public ResponseEntity<PalEntity> getByName(@RequestParam String name) {
        return palService.getByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-type")
    public List<PalEntity> getByType(@RequestParam String type) {
        return palService.getByType(type);
    }

    @GetMapping
    public List<PalEntity> getAll() {
        return palService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PalEntity create(@RequestBody PalEntity pal) {
        return palService.save(pal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PalEntity> update(@PathVariable int id, @RequestBody PalEntity pal) {
        return palService.getById(id)
                .map(existing -> {
                    pal.setId(id);
                    return ResponseEntity.ok(palService.save(pal));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        palService.deleteById(id);
    }

    @GetMapping("/{id}/skills")
    public List<PalSkillEntity> getSkills(@PathVariable int id) {
        return palService.getSkills(id);
    }

    @PostMapping("/{id}/skills")
    @ResponseStatus(HttpStatus.CREATED)
    public PalSkillEntity addSkill(@PathVariable int id, @RequestBody PalSkillEntity skill) {
        return palService.addSkill(id, skill);
    }

    @PutMapping("/{id}/skills/{skillId}")
    public PalSkillEntity modifySkill(@PathVariable int id,
                                      @PathVariable long skillId,
                                      @RequestBody PalSkillEntity skill) {
        return palService.modifySkill(id, skillId, skill);
    }

    @GetMapping("/{id}/types")
    public List<PalTypeEntity> getTypes(@PathVariable int id) {
        return palService.getTypes(id);
    }

    @PostMapping("/{id}/types")
    @ResponseStatus(HttpStatus.CREATED)
    public PalTypeEntity addType(@PathVariable int id, @RequestBody String type) {
        return palService.addType(id, type);
    }

    @DeleteMapping("/{id}/types/{typeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeType(@PathVariable int id, @PathVariable long typeId) {
        palService.removeType(id, typeId);
    }

    @GetMapping("/sorted/rarity")
    public List<PalEntity> getSortedByRarity(@RequestParam int limit) {
        return palService.getSortedByRarity(limit);
    }

    @GetMapping("/sorted/price")
    public List<PalEntity> getSortedByPrice(@RequestParam int limit) {
        return palService.getSortedByPrice(limit);
    }
}
