package com.luis.ifpark.dtos.estacionamento;

import com.luis.ifpark.dtos.campus.CampusDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstacionamentoCreateDTO {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotNull(message = "Capacidade total é obrigatória")
    @Positive(message = "Capacidade total deve ser um número positivo")
    private Integer capacidadeTotal;

    @NotNull(message = "Campus é obrigatório")
    private CampusDTO campus;
}
