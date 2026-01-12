package com.luis.ifpark.dtos.pessoa;

import com.luis.ifpark.entities.Pessoa;
import com.luis.ifpark.entities.enums.TipoPessoa;
import com.luis.ifpark.entities.enums.StatusPessoa;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PessoaResumoDTO {
    private UUID id;
    private String nome;
    private String matricula;
    private TipoPessoa tipo;
    private StatusPessoa status;
    private String telefone;

    public PessoaResumoDTO(Pessoa entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.matricula = entity.getMatricula();
        this.tipo = entity.getTipo();
        this.status = entity.getStatus();
        this.telefone = entity.getTelefone();
    }
}