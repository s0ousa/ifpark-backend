package com.luis.ifpark.repositories;

import com.luis.ifpark.entities.Estacionamento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.UUID;

@Repository
public interface EstacionamentoRepository extends JpaRepository<Estacionamento, UUID> {
    Page<Estacionamento> findByCampusId(UUID campusId, Pageable pageable);
}
