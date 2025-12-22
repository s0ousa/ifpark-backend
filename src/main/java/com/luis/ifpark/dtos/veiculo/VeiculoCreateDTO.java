package com.luis.ifpark.dtos.veiculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VeiculoCreateDTO {

    @NotBlank(message = "Placa é obrigatória")
    @Pattern(regexp = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$",
            message = "Placa inválida. Formato aceito: ABC1D23 (Mercosul) ou ABC1234 (antigo)")
    private String placa;

    @NotBlank(message = "Modelo é obrigatório")
    private String modelo;

    @NotNull(message = "ID da pessoa é obrigatório")
    private UUID pessoaId;
}