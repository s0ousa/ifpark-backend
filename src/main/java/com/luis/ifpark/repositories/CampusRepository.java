package com.luis.ifpark.repositories;

import com.luis.ifpark.dtos.campus.CampusResponseDTO;
import com.luis.ifpark.entities.Campus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CampusRepository extends JpaRepository<Campus, UUID> {

    @Query("""
        SELECT new com.luis.ifpark.dtos.campus.CampusResponseDTO(
            c.id, 
            c.nome, 
            c.endereco,
            (SELECT COUNT(u) FROM Usuario u WHERE u.campus.id = c.id),
            (SELECT COUNT(e) FROM Estacionamento e WHERE e.campus.id = c.id),
            (SELECT COALESCE(SUM(e.capacidadeTotal), 0) FROM Estacionamento e WHERE e.campus.id = c.id),
            (SELECT COUNT(m) FROM Movimentacao m WHERE m.estacionamento.campus.id = c.id AND m.dataSaida IS NULL)
        )
        FROM Campus c
        """)
    Page<CampusResponseDTO> findAllWithStats(Pageable pageable);

    @Query("""
        SELECT new com.luis.ifpark.dtos.campus.CampusResponseDTO(
            c.id, 
            c.nome, 
            c.endereco,
            (SELECT COUNT(u) FROM Usuario u WHERE u.campus.id = c.id),
            (SELECT COUNT(e) FROM Estacionamento e WHERE e.campus.id = c.id),
            (SELECT COALESCE(SUM(e.capacidadeTotal), 0) FROM Estacionamento e WHERE e.campus.id = c.id),
            (SELECT COUNT(m) FROM Movimentacao m WHERE m.estacionamento.campus.id = c.id AND m.dataSaida IS NULL)
        )
        FROM Campus c WHERE c.id = :id
        """)
    Optional<CampusResponseDTO> findByIdWithStats(@Param("id") UUID id);
}
