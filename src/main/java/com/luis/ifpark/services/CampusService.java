package com.luis.ifpark.services;

import com.luis.ifpark.dtos.campus.CampusCreateDTO;
import com.luis.ifpark.dtos.campus.CampusDTO;
import com.luis.ifpark.dtos.campus.CampusUpdateDTO;
import com.luis.ifpark.entities.Campus;
import com.luis.ifpark.entities.Endereco;
import com.luis.ifpark.exceptions.DatabaseException;
import com.luis.ifpark.exceptions.ResourceNotFoundException;
import com.luis.ifpark.repositories.CampusRepository;
import com.luis.ifpark.repositories.EnderecoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CampusService {

    @Autowired
    private CampusRepository repository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Transactional(readOnly = true)
    public CampusDTO findById(UUID id) {
        Campus campusResult = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado")
        );
        return new CampusDTO(campusResult);
    }

    @Transactional(readOnly = true)
    public Page<CampusDTO> findAll(Pageable pageable) {
        Page<Campus> result = repository.findAll(pageable);
        return result.map(CampusDTO::new);
    }

    @Transactional
    public CampusDTO insert(CampusCreateDTO dto) {
        Campus entity = new Campus();
        copyCreateDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new CampusDTO(entity);
    }

    @Transactional
    public CampusDTO update(UUID id, CampusUpdateDTO dto) {
        try {
            Campus entity = repository.getReferenceById(id);
            copyUpdateDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new CampusDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    private void copyCreateDtoToEntity(CampusCreateDTO dto, Campus entity) {
        entity.setNome(dto.getNome());

        if (dto.getEndereco().getId() != null) {
            // Usar endereço existente
            Endereco endereco = enderecoRepository.findById(dto.getEndereco().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));
            entity.setEndereco(endereco);
        } else {
            Endereco endereco = new Endereco();
            endereco.setLogradouro(dto.getEndereco().getLogradouro());
            endereco.setNumero(dto.getEndereco().getNumero());
            endereco.setComplemento(dto.getEndereco().getComplemento());
            endereco.setBairro(dto.getEndereco().getBairro());
            endereco.setCidade(dto.getEndereco().getCidade());
            endereco.setEstado(dto.getEndereco().getEstado());
            endereco.setCep(dto.getEndereco().getCep());

            endereco = enderecoRepository.save(endereco);
            entity.setEndereco(endereco);
        }
    }

    private void copyUpdateDtoToEntity(CampusUpdateDTO dto, Campus entity) {

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            entity.setNome(dto.getNome());
        }

        if (dto.getEndereco() != null) {
            if (dto.getEndereco().getId() != null) {
                Endereco endereco = enderecoRepository.findById(dto.getEndereco().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));
                entity.setEndereco(endereco);
            } else {
                Endereco endereco = new Endereco();
                endereco.setLogradouro(dto.getEndereco().getLogradouro());
                endereco.setNumero(dto.getEndereco().getNumero());
                endereco.setComplemento(dto.getEndereco().getComplemento());
                endereco.setBairro(dto.getEndereco().getBairro());
                endereco.setCidade(dto.getEndereco().getCidade());
                endereco.setEstado(dto.getEndereco().getEstado());
                endereco.setCep(dto.getEndereco().getCep());

                endereco = enderecoRepository.save(endereco);
                entity.setEndereco(endereco);
            }
        }
    }
}