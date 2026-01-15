package com.luis.ifpark.dtos.estacionamento;

import com.luis.ifpark.dtos.campus.CampusDTO;
import com.luis.ifpark.dtos.campus.CampusResumoDTO;
import com.luis.ifpark.entities.Estacionamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstacionamentoDTO {
    private UUID id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotNull(message = "Capacidade total é obrigatória")
    @Positive(message = "Capacidade total deve ser um número positivo")
    private Integer capacidadeTotal;

    @NotNull(message = "Campus é obrigatório")
    private CampusResumoDTO campus;
    
    private boolean ativo;

    public EstacionamentoDTO(Estacionamento entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.capacidadeTotal = entity.getCapacidadeTotal();
        this.campus = entity.getCampus() != null ? new CampusResumoDTO(entity.getCampus()) : null;
        this.ativo = entity.getAtivo();
    }
}
