package com.luis.ifpark.dtos.movimentacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class ExitRegisterDTO {
    @NotBlank(message = "A placa do veículo é obrigatória para registrar a saída.")
    @Pattern(regexp = "[A-Z]{3}[0-9][0-9A-Z][0-9]{2}", message = "A placa está em um formato inválido.")
    private String placa;
}
