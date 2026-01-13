package com.luis.ifpark.dtos.usuario;

import com.luis.ifpark.dtos.endereco.EnderecoDTO;
import com.luis.ifpark.entities.Usuario;
import com.luis.ifpark.entities.enums.PapelUsuario;
import com.luis.ifpark.dtos.pessoa.PessoaResponseDTO;
import com.luis.ifpark.dtos.campus.CampusDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private UUID id;
    private String email;
    private PapelUsuario papel;
    private PessoaResponseDTO pessoa;
    private CampusDTO campus;
    private EnderecoDTO endereco;

    public UsuarioResponseDTO(Usuario entity) {
        this.id = entity.getId();
        this.email = entity.getEmail();
        this.papel = entity.getPapel();
        this.pessoa = entity.getPessoa() != null ? new PessoaResponseDTO(entity.getPessoa()) : null;
        this.campus = entity.getCampus() != null ? new CampusDTO(entity.getCampus()) : null;

        if (entity.getPessoa() != null && entity.getPessoa().getEndereco() != null) {
            this.endereco = new EnderecoDTO(entity.getPessoa().getEndereco());
        }
    }
}