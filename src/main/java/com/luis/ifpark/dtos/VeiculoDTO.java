package com.luis.ifpark.dtos;

import com.luis.ifpark.dtos.pessoa.PessoaUpdateDTO;
import com.luis.ifpark.entities.Veiculo;
import com.luis.ifpark.entities.enums.StatusAprovacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeiculoDTO {
    private UUID id;

    @NotBlank(message = "Placa é obrigatória")
    @Size(min = 7, max = 8, message = "Placa deve ter entre 7 e 8 caracteres")
    private String placa;

    @NotBlank(message = "Modelo é obrigatório")
    @Size(min = 2, max = 50, message = "Modelo deve ter entre 2 e 50 caracteres")
    private String modelo;

    @NotNull(message = "Status de aprovação é obrigatório")
    private StatusAprovacao statusAprovacao;

    @Size(max = 200, message = "Motivo de rejeição deve ter no máximo 200 caracteres")
    private String motivoRejeicao;

    @NotNull(message = "Pessoa é obrigatória")
    private PessoaUpdateDTO.PessoaDTO pessoa;

    // Não incluí a lista de movimentações para evitar loops infinitos
    // Esse relacionamento pode ser tratado separadamente se necessário

    public VeiculoDTO(Veiculo entity) {
        this.id = entity.getId();
        this.placa = entity.getPlaca();
        this.modelo = entity.getModelo();
        this.statusAprovacao = entity.getStatusAprovacao();
        this.motivoRejeicao = entity.getMotivoRejeicao();
        this.pessoa = entity.getPessoa() != null ? new PessoaUpdateDTO.PessoaDTO(entity.getPessoa()) : null;
    }
}
