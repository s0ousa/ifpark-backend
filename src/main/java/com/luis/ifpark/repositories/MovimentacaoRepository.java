package com.luis.ifpark.repositories;

import com.luis.ifpark.entities.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, UUID> {
    List<Movimentacao> findByEstacionamentoIdAndDataSaidaIsNull(UUID estacionamentoId);
}
