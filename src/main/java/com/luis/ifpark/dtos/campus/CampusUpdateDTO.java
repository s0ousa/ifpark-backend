package com.luis.ifpark.dtos.campus;

import com.luis.ifpark.dtos.endereco.EnderecoDTO;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampusUpdateDTO {
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    private EnderecoDTO endereco;
}