package com.luis.ifpark.repositories;

import com.luis.ifpark.entities.Estacionamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EstacionamentoRepository extends JpaRepository<Estacionamento, UUID> {
}
