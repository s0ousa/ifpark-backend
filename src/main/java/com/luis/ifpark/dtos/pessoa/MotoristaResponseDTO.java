package com.luis.ifpark.dtos.pessoa;

import com.luis.ifpark.dtos.veiculo.VeiculoResumoDTO;
import com.luis.ifpark.entities.enums.StatusPessoa;
import com.luis.ifpark.entities.enums.TipoPessoa;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MotoristaResponseDTO {
    private UUID id;
    private String nome;
    private String cpf;
    private TipoPessoa tipo;
    private String telefone;
    private StatusPessoa status;
    private List<VeiculoResumoDTO> veiculos;
}
