package com.luis.ifpark.dtos.usuario;

import com.luis.ifpark.dtos.pessoa.PessoaResumoDTO;
import com.luis.ifpark.entities.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VigiaDTO {

    private UUID id;
    private String email;
    private String papel;
    private PessoaResumoDTO pessoa;

    public VigiaDTO(Usuario entity) {
        this.id = entity.getId();
        this.email = entity.getEmail();
        if (entity.getPessoa() != null) {
            this.pessoa = new PessoaResumoDTO(entity.getPessoa());
        }
    }
}