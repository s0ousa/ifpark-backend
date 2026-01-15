package com.luis.ifpark.dtos.veiculo;

import com.luis.ifpark.entities.Veiculo;
import com.luis.ifpark.entities.enums.StatusAprovacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VeiculoResumoDTO {
    private UUID id;
    private String placa;
    private String modelo;
    private StatusAprovacao StatusAprovacao;

    public VeiculoResumoDTO(Veiculo veiculo){
        this.id = veiculo.getId();
        this.placa = veiculo.getPlaca();
        this.modelo = veiculo.getModelo();
        this.StatusAprovacao = veiculo.getStatusAprovacao();
    }
}
