package com.luis.ifpark.services;

import com.luis.ifpark.dtos.estacionamento.EstacionamentoDTO;
import com.luis.ifpark.dtos.estacionamento.EstacionamentoCreateDTO;
import com.luis.ifpark.dtos.estacionamento.EstacionamentoUpdateDTO;
import com.luis.ifpark.entities.Campus;
import com.luis.ifpark.entities.Estacionamento;
import com.luis.ifpark.exceptions.DatabaseException;
import com.luis.ifpark.exceptions.ResourceNotFoundException;
import com.luis.ifpark.repositories.CampusRepository;
import com.luis.ifpark.repositories.EstacionamentoRepository;
import com.luis.ifpark.utils.SecurityUtils;
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
public class EstacionamentoService {

    @Autowired
    private EstacionamentoRepository repository;
    
    @Autowired
    private CampusRepository campusRepository;

    @Transactional(readOnly = true)
    public EstacionamentoDTO findById(UUID id) {
        Estacionamento estacionamentoResult = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado")
        );
        return new EstacionamentoDTO(estacionamentoResult);
    }

    @Transactional(readOnly = true)
    public Page<EstacionamentoDTO> findAll(Pageable pageable) {
        Page<Estacionamento> result = repository.findAll(pageable);
        return result.map(estacionamento -> new EstacionamentoDTO(estacionamento));
    }

    @Transactional
    public EstacionamentoDTO insert(EstacionamentoCreateDTO dto) {
        // Verificar se o usuário tem acesso ao campus onde está criando o estacionamento
        if (dto.getCampus() != null && dto.getCampus().getId() != null) {
            if (!SecurityUtils.isSuperAdmin() && !SecurityUtils.hasAccessToCampus(dto.getCampus().getId())) {
                throw new SecurityException("Você não tem permissão para criar estacionamentos neste campus");
            }
        }
        
        Estacionamento entity = new Estacionamento();
        copyCreateDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new EstacionamentoDTO(entity);
    }

    @Transactional
    public EstacionamentoDTO update(UUID id, EstacionamentoUpdateDTO dto) {
        Estacionamento estacionamento = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Estacionamento não encontrado"));
        
        // Verificar se o usuário tem acesso ao campus do estacionamento
        if (estacionamento.getCampus() != null) {
            if (!SecurityUtils.isSuperAdmin() && !SecurityUtils.hasAccessToCampus(estacionamento.getCampus().getId())) {
                throw new SecurityException("Você não tem permissão para modificar este estacionamento");
            }
        }
        
        try {
            Estacionamento entity = repository.getReferenceById(id);
            copyUpdateDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new EstacionamentoDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(UUID id) {
        Estacionamento estacionamento = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Estacionamento não encontrado"));
        
        // Verificar se o usuário tem acesso ao campus do estacionamento
        if (estacionamento.getCampus() != null) {
            if (!SecurityUtils.isSuperAdmin() && !SecurityUtils.hasAccessToCampus(estacionamento.getCampus().getId())) {
                throw new SecurityException("Você não tem permissão para deletar este estacionamento");
            }
        }
        
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    private void copyCreateDtoToEntity(EstacionamentoCreateDTO dto, Estacionamento entity) {
        entity.setNome(dto.getNome());
        entity.setCapacidadeTotal(dto.getCapacidadeTotal());
        
        // Verifica se o campus foi fornecido no DTO
        if (dto.getCampus() != null && dto.getCampus().getId() != null) {
            Campus campus = campusRepository.findById(dto.getCampus().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Campus não encontrado"));
            entity.setCampus(campus);
        }
    }
    
    private void copyUpdateDtoToEntity(EstacionamentoUpdateDTO dto, Estacionamento entity) {
        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            entity.setNome(dto.getNome());
        }
        
        if (dto.getCapacidadeTotal() != null) {
            entity.setCapacidadeTotal(dto.getCapacidadeTotal());
        }
        
        if (dto.getCampus() != null && dto.getCampus().getId() != null) {
            Campus campus = campusRepository.findById(dto.getCampus().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Campus não encontrado"));
            entity.setCampus(campus);
        }
    }
}
