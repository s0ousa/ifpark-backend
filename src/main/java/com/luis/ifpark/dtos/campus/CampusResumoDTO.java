package com.luis.ifpark.dtos.campus;

import com.luis.ifpark.entities.Campus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampusResumoDTO {
    private UUID id;
    private String nome;

    public CampusResumoDTO(Campus entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
    }
}