package com.luis.ifpark.dtos.pessoa;

import com.luis.ifpark.dtos.endereco.EnderecoDTO;
import com.luis.ifpark.entities.Pessoa;
import com.luis.ifpark.entities.enums.TipoPessoa;
import com.luis.ifpark.entities.enums.StatusPessoa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.util.UUID;

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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PessoaDTO {
        private UUID id;

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        private String nome;

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato XXX.XXX.XXX-XX")
        @Size(min = 14, max = 14, message = "CPF deve ter 14 caracteres")
        private String cpf;

        @Size(min = 0, max = 20, message = "Matrícula deve ter no máximo 20 caracteres")
        private String matricula;

        @NotNull(message = "Tipo é obrigatório")
        private TipoPessoa tipo;

        @NotNull(message = "Status é obrigatório")
        private StatusPessoa status;

        @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}", message = "Telefone deve estar no formato (XX) XXXXX-XXXX")
        @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
        private String telefone;

        private EnderecoDTO endereco;

        // Não incluí Usuario nem Veiculos para evitar loops infinitos
        // Esses relacionamentos podem ser tratados separadamente se necessário

        public PessoaDTO(Pessoa entity) {
            this.id = entity.getId();
            this.nome = entity.getNome();
            this.cpf = entity.getCpf();
            this.matricula = entity.getMatricula();
            this.tipo = entity.getTipo();
            this.status = entity.getStatus();
            this.telefone = entity.getTelefone();
            this.endereco = entity.getEndereco() != null ? new EnderecoDTO(entity.getEndereco()) : null;
        }
    }
}
