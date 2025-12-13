package com.luis.ifpark.services;

import com.luis.ifpark.dtos.endereco.EnderecoDTO;
import com.luis.ifpark.dtos.endereco.EnderecoUpdateDTO;
import com.luis.ifpark.entities.Endereco;
import com.luis.ifpark.exceptions.ResourceNotFoundException;
import com.luis.ifpark.repositories.EnderecoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository repository;

    @Transactional(readOnly = true)
    public EnderecoDTO findById(UUID id) {
        Endereco enderecoResult = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado")
        );
        return new EnderecoDTO(enderecoResult);
    }

    @Transactional
    public EnderecoDTO update(UUID id, EnderecoUpdateDTO dto) {
        try {
            Endereco entity = repository.getReferenceById(id);
            copyUpdateDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new EnderecoDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }

    private void copyUpdateDtoToEntity(EnderecoUpdateDTO dto, Endereco entity) {
        if (dto.getLogradouro() != null && !dto.getLogradouro().isBlank()) {
            entity.setLogradouro(dto.getLogradouro());
        }

        if (dto.getNumero() != null && !dto.getNumero().isBlank()) {
            entity.setNumero(dto.getNumero());
        }

        if (dto.getComplemento() != null) {
            entity.setComplemento(dto.getComplemento());
        }

        if (dto.getBairro() != null && !dto.getBairro().isBlank()) {
            entity.setBairro(dto.getBairro());
        }

        if (dto.getCidade() != null && !dto.getCidade().isBlank()) {
            entity.setCidade(dto.getCidade());
        }

        if (dto.getEstado() != null && !dto.getEstado().isBlank()) {
            entity.setEstado(dto.getEstado());
        }

        if (dto.getCep() != null && !dto.getCep().isBlank()) {
            entity.setCep(dto.getCep());
        }
    }
}
