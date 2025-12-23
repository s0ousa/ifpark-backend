package com.luis.ifpark.repositories;

import com.luis.ifpark.entities.Estacionamento;
import com.luis.ifpark.entities.Movimentacao;
import com.luis.ifpark.entities.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, UUID> {
    List<Movimentacao> findByEstacionamentoIdAndDataSaidaIsNull(UUID estacionamentoId);

    boolean existsByVeiculoAndDataSaidaIsNull(Veiculo veiculo);

    Optional<Movimentacao> findFirstByVeiculoAndDataSaidaIsNull(Veiculo veiculo);

    // Conta quantos carros estão atualmente no estacionamento sem data de saída
    long countByEstacionamentoAndDataSaidaIsNull(Estacionamento estacionamento);
}
