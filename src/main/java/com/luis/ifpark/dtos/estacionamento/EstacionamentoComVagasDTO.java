package com.luis.ifpark.dtos.estacionamento;

import com.luis.ifpark.dtos.campus.CampusResumoDTO;
import com.luis.ifpark.entities.Estacionamento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class EstacionamentoComVagasDTO implements Serializable {
    private UUID id;
    private String nome;
    private Integer capacidadeTotal;
    private Integer vagasOcupadas;
    private Integer vagasLivres;
    private CampusResumoDTO campus;

    public EstacionamentoComVagasDTO(Estacionamento entity, long vagasOcupadasCount) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.capacidadeTotal = entity.getCapacidadeTotal();
        this.vagasOcupadas = (int) vagasOcupadasCount;

        this.vagasLivres = this.capacidadeTotal - this.vagasOcupadas;

        if (entity.getCampus() != null) {
            this.campus = new CampusResumoDTO(entity.getCampus());
        }
    }
}