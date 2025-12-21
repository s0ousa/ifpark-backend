package com.luis.ifpark.dtos.pessoa;

import com.luis.ifpark.dtos.endereco.EnderecoDTO;
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
public class PessoaResponseDTO {
    private UUID id;
    private String nome;
    private String cpf;
    private String matricula;
    private TipoPessoa tipo;
    private StatusPessoa status;
    private String telefone;
    
    // Não incluí relacionamentos para evitar loops infinitos
    // Podem ser tratados separadamente se necessário

    public PessoaResponseDTO(Pessoa entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.cpf = entity.getCpf();
        this.matricula = entity.getMatricula();
        this.tipo = entity.getTipo();
        this.status = entity.getStatus();
        this.telefone = entity.getTelefone();
    }
}
