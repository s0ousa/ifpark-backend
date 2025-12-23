package com.luis.ifpark.dtos.movimentacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class EntryRegisterDTO {
    @NotBlank(message = "A placa do veículo é obrigatória.")
    @Size(min = 7, max = 7, message = "A placa deve ter exatamente 7 caracteres.")
    @Pattern(regexp = "[A-Z]{3}[0-9][0-9A-Z][0-9]{2}", message = "A placa está em um formato inválido.")
    private String placa;

    @NotNull(message = "O ID do estacionamento é obrigatório.")
    private UUID estacionamentoId;
}