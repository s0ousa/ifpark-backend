package com.luis.ifpark.dtos.endereco;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoUpdateDTO {
    @Size(min = 3, max = 100, message = "Logradouro deve ter entre 3 e 100 caracteres")
    private String logradouro;

    @Size(min = 1, max = 10, message = "Número deve ter entre 1 e 10 caracteres")
    private String numero;

    @Size(max = 50, message = "Complemento deve ter no máximo 50 caracteres")
    private String complemento;

    @Size(min = 2, max = 50, message = "Bairro deve ter entre 2 e 50 caracteres")
    private String bairro;

    @Size(min = 2, max = 50, message = "Cidade deve ter entre 2 e 50 caracteres")
    private String cidade;

    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    private String estado;

    @Size(min = 9, max = 9, message = "CEP deve ter 9 caracteres no formato XXXXX-XXX")
    private String cep;
}
