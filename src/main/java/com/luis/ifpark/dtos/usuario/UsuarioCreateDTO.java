package com.luis.ifpark.dtos.usuario;

import com.luis.ifpark.entities.enums.PapelUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCreateDTO {
    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    @Size(min = 5, max = 100, message = "Email deve ter entre 5 e 100 caracteres")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotNull(message = "Papel é obrigatório")
    private PapelUsuario papel;

    @NotNull(message = "ID da Pessoa é obrigatório")
    private UUID pessoaId;
}
