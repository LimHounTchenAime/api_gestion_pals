package com.example.api_gestion_pals.DAO;

import com.example.api_gestion_pals.model.PalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PalDAO extends JpaRepository<PalEntity, Integer> {

    Optional<PalEntity> findByNameIgnoreCase(String name);

    List<PalEntity> findByTypes_TypeIgnoreCase(String type);

}
