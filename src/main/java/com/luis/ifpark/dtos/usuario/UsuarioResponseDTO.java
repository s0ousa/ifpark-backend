package com.luis.ifpark.dtos.usuario;

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
}
