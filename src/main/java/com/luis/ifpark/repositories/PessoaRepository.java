package com.luis.ifpark.repositories;

import com.luis.ifpark.entities.Pessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {
    boolean existsByCpf(String cpf);
    Optional<Pessoa> findByCpf(String cpf);


    // VISITANTE ou ROLE_COMUM
    @Query("""
        SELECT p.id 
        FROM Pessoa p
        LEFT JOIN Usuario u ON u.pessoa.id = p.id
        WHERE p.tipo = 'VISITANTE' 
           OR u.papel = 'ROLE_COMUM'
    """)
    Page<UUID> findIdsMotoristas(Pageable pageable);

    @Query("""
        SELECT DISTINCT p
        FROM Pessoa p
        LEFT JOIN FETCH p.veiculos
        WHERE p.id IN :ids
    """)
    List<Pessoa> findPessoasComVeiculosPorIds(@Param("ids") List<UUID> ids);
}
