package com.luis.ifpark.dtos.movimentacao;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MovimentacaoResponseDTO {
    private UUID id;
    private String placa;
    private String modelo;

    private UUID estacionamentoId;
    private String nomeEstacionamento;

    private LocalDateTime dataEntrada;
    private LocalDateTime dataSaida;

    private UUID vigiaEntradaId;
    private String nomeVigiaEntrada;

    private UUID vigiaSaidaId;
    private String nomeVigiaSaida;
}