package com.luis.ifpark.dtos.campus;

import com.luis.ifpark.dtos.endereco.EnderecoDTO;
import com.luis.ifpark.entities.Campus;
import com.luis.ifpark.entities.Endereco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CampusResponseDTO {
    private UUID id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotNull(message = "Endereço é obrigatório")
    private EnderecoDTO endereco;

    private Integer totalUsuarios;
    private Integer quantidadeEstacionamentos;
    private Integer totalVagas;
    private Integer totalVagasOcupadas;
    private Integer totalVagasLivres;

    // Construtor para DTO Projection (usado pela query JPQL)
    public CampusResponseDTO(UUID id, String nome, Endereco endereco,
                             Long totalUsuarios, Long quantidadeEstacionamentos,
                             Long totalVagas, Long totalVagasOcupadas) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco != null ? new EnderecoDTO(endereco) : null;
        this.totalUsuarios = totalUsuarios.intValue();
        this.quantidadeEstacionamentos = quantidadeEstacionamentos.intValue();
        this.totalVagas = totalVagas.intValue();
        this.totalVagasOcupadas = totalVagasOcupadas.intValue();
        this.totalVagasLivres = this.totalVagas - this.totalVagasOcupadas;
    }

}
