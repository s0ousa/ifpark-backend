package com.luis.ifpark.dtos;

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
    private UsuarioDTO vigiaEntrada;

    private UsuarioDTO vigiaSaida;

    public MovimentacaoDTO(Movimentacao entity) {
        this.id = entity.getId();
        this.dataEntrada = entity.getDataEntrada();
        this.dataSaida = entity.getDataSaida();
        this.veiculo = entity.getVeiculo() != null ? new VeiculoDTO(entity.getVeiculo()) : null;
        this.estacionamento = entity.getEstacionamento() != null ? new EstacionamentoDTO(entity.getEstacionamento()) : null;
        this.vigiaEntrada = entity.getVigiaEntrada() != null ? new UsuarioDTO(entity.getVigiaEntrada()) : null;
        this.vigiaSaida = entity.getVigiaSaida() != null ? new UsuarioDTO(entity.getVigiaSaida()) : null;
    }
}
