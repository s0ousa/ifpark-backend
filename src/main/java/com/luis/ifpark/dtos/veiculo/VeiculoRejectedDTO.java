package com.luis.ifpark.dtos.veiculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeiculoRejectedDTO {

    @NotBlank(message = "Motivo é obrigatório")
    @Size(min = 10, max = 200, message = "Motivo deve ter entre 10 e 200 caracteres")
    private String motivo;
}