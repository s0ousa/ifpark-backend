package com.luis.ifpark.dtos.movimentacao;

import com.luis.ifpark.dtos.estacionamento.EstacionamentoDTO;
import com.luis.ifpark.dtos.usuario.VigiaDTO;
import com.luis.ifpark.dtos.usuario.VigiaResumoDTO;
import com.luis.ifpark.dtos.veiculo.VeiculoDTO;
import com.luis.ifpark.dtos.veiculo.VeiculoResumoDTO;
import com.luis.ifpark.entities.Movimentacao;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoDTO {
    private UUID id;

    @NotNull(message = "Data de entrada é obrigatória")
    private LocalDateTime dataEntrada;

    private LocalDateTime dataSaida;

    @NotNull(message = "Veículo é obrigatório")
    private VeiculoDTO veiculo;

    @NotNull(message = "Estacionamento é obrigatório")
    private EstacionamentoDTO estacionamento;

    @NotNull(message = "Vigia de entrada é obrigatório")
    private VigiaResumoDTO vigiaEntrada;

    private VigiaResumoDTO vigiaSaida;

    public MovimentacaoDTO(Movimentacao entity) {
        this.id = entity.getId();
        this.dataEntrada = entity.getDataEntrada();
        this.dataSaida = entity.getDataSaida();
        this.veiculo = entity.getVeiculo() != null ? new VeiculoDTO(entity.getVeiculo()) : null;
        this.estacionamento = entity.getEstacionamento() != null ? new EstacionamentoDTO(entity.getEstacionamento()) : null;
        this.vigiaEntrada = entity.getVigiaEntrada() != null ? new VigiaResumoDTO(entity.getVigiaEntrada()) : null;
        this.vigiaSaida = entity.getVigiaSaida() != null ? new VigiaResumoDTO(entity.getVigiaSaida()) : null;
    }
}