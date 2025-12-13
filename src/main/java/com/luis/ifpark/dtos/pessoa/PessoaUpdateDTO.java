package com.luis.ifpark.dtos.pessoa;

import com.luis.ifpark.entities.enums.TipoPessoa;
import com.luis.ifpark.entities.enums.StatusPessoa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PessoaUpdateDTO {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @CPF(message = "CPF deve ser válido")
    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @Size(max = 20, message = "Matrícula deve ter no máximo 20 caracteres")
    private String matricula;

    @NotNull(message = "Tipo é obrigatório")
    private TipoPessoa tipo;

    @NotNull(message = "Status é obrigatório")
    private StatusPessoa status;

    @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}", message = "Telefone deve estar no formato (XX) XXXXX-XXXX")
    @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
    private String telefone;
}
