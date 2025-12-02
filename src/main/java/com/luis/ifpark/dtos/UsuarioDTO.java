package com.luis.ifpark.dtos;

import com.luis.ifpark.entities.Usuario;
import com.luis.ifpark.entities.enums.PapelUsuario;
import jakarta.validation.constraints.Email;
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
public class UsuarioDTO {
    private UUID id;

    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    @Size(min = 5, max = 100, message = "Email deve ter entre 5 e 100 caracteres")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    private String senha;

    @NotNull(message = "Papel é obrigatório")
    private PapelUsuario papel;

    @NotNull(message = "Pessoa é obrigatória")
    private PessoaDTO pessoa;

    public UsuarioDTO(Usuario entity) {
        this.id = entity.getId();
        this.email = entity.getEmail();
        this.senha = entity.getSenha();
        this.papel = entity.getPapel();
        this.pessoa = entity.getPessoa() != null ? new PessoaDTO(entity.getPessoa()) : null;
    }
}
