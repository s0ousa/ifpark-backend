package com.luis.ifpark.repositories;

import com.luis.ifpark.entities.Estacionamento;
import com.luis.ifpark.entities.Movimentacao;
import com.luis.ifpark.entities.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, UUID> {

    @Query("SELECT m FROM Movimentacao m ORDER BY COALESCE(m.dataSaida, m.dataEntrada) DESC")
    Page<Movimentacao> findAll(Pageable pageable);

    // Busca por estacionamento com ordenação por última atualização
    @Query("SELECT m FROM Movimentacao m WHERE m.estacionamento.id = :estacionamentoId ORDER BY COALESCE(m.dataSaida, m.dataEntrada) DESC")
    Page<Movimentacao> findByEstacionamentoId(UUID estacionamentoId, Pageable pageable);

    List<Movimentacao> findByEstacionamentoIdAndDataSaidaIsNull(UUID estacionamentoId);
    Page<Movimentacao> findByEstacionamentoIdAndDataSaidaIsNull(UUID estacionamentoId, Pageable pageable);

    boolean existsByVeiculoAndDataSaidaIsNull(Veiculo veiculo);

    Optional<Movimentacao> findFirstByVeiculoAndDataSaidaIsNull(Veiculo veiculo);

    // Conta quantos carros estão atualmente no estacionamento sem data de saída
    long countByEstacionamentoAndDataSaidaIsNull(Estacionamento estacionamento);

    long countByEstacionamentoIdAndDataSaidaIsNull(UUID estacionamentoId);
}
