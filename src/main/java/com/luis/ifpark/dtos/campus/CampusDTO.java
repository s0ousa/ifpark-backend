package com.luis.ifpark.dtos.campus;

import com.luis.ifpark.dtos.endereco.EnderecoDTO;
import com.luis.ifpark.entities.Campus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CampusDTO {
    private UUID id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotNull(message = "Endereço é obrigatório")
    private EnderecoDTO endereco;
    
    private boolean ativo;

    // Para simplificar, não incluí a lista de estacionamentos no DTO
    // Isso evita problemas de serialização e performance

    public CampusDTO() {
    }

    public CampusDTO(UUID id, String nome, EnderecoDTO endereco) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
    }

    public CampusDTO(Campus entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.endereco = entity.getEndereco() != null ? new EnderecoDTO(entity.getEndereco()) : null;
        this.ativo = entity.getAtivo();
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public EnderecoDTO getEndereco() {
        return endereco;
    }
}
