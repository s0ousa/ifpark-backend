package com.luis.ifpark.dtos.estacionamento;

import com.luis.ifpark.dtos.campus.CampusDTO;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstacionamentoUpdateDTO {
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    private Integer capacidadeTotal;

    private CampusDTO campus;
}
