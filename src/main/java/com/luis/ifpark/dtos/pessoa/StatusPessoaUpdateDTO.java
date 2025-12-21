package com.luis.ifpark.dtos.pessoa;

import com.luis.ifpark.entities.enums.StatusPessoa;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StatusPessoaUpdateDTO {

    @NotNull(message = "Status é obrigatório")
    private StatusPessoa status;

}