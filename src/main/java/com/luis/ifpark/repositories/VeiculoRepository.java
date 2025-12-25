package com.luis.ifpark.repositories;

import com.luis.ifpark.entities.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, UUID> {
    boolean existsByPlaca(String placa);
    Optional<Veiculo> findByPlacaIgnoreCase(String placa);
    List<Veiculo> findByPessoaId(UUID pessoaId);

    @Query("SELECT v FROM Veiculo v WHERE v.pessoa.usuario.campus.id = :campusId")
    List<Veiculo> findByCampusId(@Param("campusId") UUID campusId);
}
