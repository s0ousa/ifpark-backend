package com.luis.ifpark.repositories;

import com.luis.ifpark.entities.Estacionamento;
import com.luis.ifpark.entities.Movimentacao;
import com.luis.ifpark.entities.Veiculo;
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
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, UUID> {

    @Query("SELECT m FROM Movimentacao m WHERE m.veiculo.pessoa.usuario.id = :usuarioId")
    Page<Movimentacao> findByUsuarioId(@Param("usuarioId") UUID usuarioId, Pageable pageable);


    @Query("SELECT m FROM Movimentacao m WHERE m.veiculo.pessoa.usuario.id = :usuarioId " +
            "AND (:estacionamentoId IS NULL OR m.estacionamento.id = :estacionamentoId)")
    Page<Movimentacao> findByUsuarioIdAndEstacionamentoId(
            @Param("usuarioId") UUID usuarioId,
            @Param("estacionamentoId") UUID estacionamentoId,
            Pageable pageable
    );

    @Query("SELECT m FROM Movimentacao m ORDER BY COALESCE(m.dataSaida, m.dataEntrada) DESC")
    Page<Movimentacao> findAll(Pageable pageable);

    // Busca por estacionamento com ordenação por última atualização
    @Query("SELECT m FROM Movimentacao m WHERE m.estacionamento.id = :estacionamentoId ORDER BY COALESCE(m.dataSaida, m.dataEntrada) DESC")
    Page<Movimentacao> findByEstacionamentoId(UUID estacionamentoId, Pageable pageable);

    Page<Movimentacao> findByEstacionamentoIdAndDataSaidaIsNull(UUID estacionamentoId, Pageable pageable);

    boolean existsByVeiculoAndDataSaidaIsNull(Veiculo veiculo);

    Optional<Movimentacao> findFirstByVeiculoAndDataSaidaIsNull(Veiculo veiculo);

    // Conta quantos carros estão atualmente no estacionamento sem data de saída
    long countByEstacionamentoAndDataSaidaIsNull(Estacionamento estacionamento);

    long countByEstacionamentoIdAndDataSaidaIsNull(UUID estacionamentoId);
}
