package com.luis.ifpark.dtos.usuario;

import com.luis.ifpark.entities.Usuario;
import com.luis.ifpark.entities.enums.PapelUsuario;
import com.luis.ifpark.entities.enums.StatusPessoa;
import com.luis.ifpark.entities.enums.TipoPessoa;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateDTO {
    
    // Dados de Usuário (opcionais)
    @Email(message = "Email deve ser válido")
    @Size(min = 5, max = 100, message = "Email deve ter entre 5 e 100 caracteres")
    private String email;

    private PapelUsuario papel;
    
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;
    
    private UUID campusId;

    // Dados de Pessoa (opcionais)
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @Size(min = 14, max = 14, message = "CPF deve ter 14 caracteres")
    private String cpf;

    @Size(max = 20, message = "Matrícula deve ter no máximo 20 caracteres")
    private String matricula;

    private TipoPessoa tipo;

    private StatusPessoa status;

    @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
    private String telefone;

    // Dados de Endereço (opcionais)
    @Size(max = 100, message = "Logradouro deve ter no máximo 100 caracteres")
    private String logradouro;

    @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
    private String numero;

    @Size(max = 50, message = "Complemento deve ter no máximo 50 caracteres")
    private String complemento;

    @Size(max = 50, message = "Bairro deve ter no máximo 50 caracteres")
    private String bairro;

    @Size(max = 50, message = "Cidade deve ter no máximo 50 caracteres")
    private String cidade;

    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    private String estado;

    @Size(min = 9, max = 9, message = "CEP deve ter 9 caracteres")
    private String cep;
}
