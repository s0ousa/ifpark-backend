package com.luis.ifpark.dtos.pessoa;

import com.luis.ifpark.entities.enums.PapelUsuario;
import com.luis.ifpark.entities.enums.TipoPessoa;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.util.UUID;
@Getter
@Setter
public class VisitanteDTO {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @CPF(message = "CPF deve ser válido")
    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @NotNull(message = "Tipo é obrigatório")
    private TipoPessoa tipo = TipoPessoa.VISITANTE;

    @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}", message = "Telefone deve estar no formato (XX) XXXXX-XXXX")
    @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
    private String telefone;

    // campos de veiculo
    @NotBlank(message = "Placa é obrigatória")
    @Pattern(regexp = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$",
            message = "Placa inválida. Formato aceito: ABC1D23 (Mercosul) ou ABC1234 (antigo)")
    private String placa;

    @NotBlank(message = "Modelo é obrigatório")
    private String modelo;

    // Campos de endereço
    @NotBlank(message = "Logradouro é obrigatório")
    @Size(min = 3, max = 100, message = "Logradouro deve ter entre 3 e 100 caracteres")
    private String logradouro;

    @NotBlank(message = "Número é obrigatório")
    @Size(min = 1, max = 10, message = "Número deve ter entre 1 e 10 caracteres")
    private String numero;

    @Size(max = 50, message = "Complemento deve ter no máximo 50 caracteres")
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(min = 2, max = 50, message = "Bairro deve ter entre 2 e 50 caracteres")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(min = 2, max = 50, message = "Cidade deve ter entre 2 e 50 caracteres")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    private String estado;

    @NotBlank(message = "CEP é obrigatório")
    @Size(min = 9, max = 9, message = "CEP deve ter 9 caracteres no formato XXXXX-XXX")
    private String cep;
}
