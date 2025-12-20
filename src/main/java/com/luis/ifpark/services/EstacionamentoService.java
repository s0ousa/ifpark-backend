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

    @Transactional(readOnly = true)
    public Page<EstacionamentoDTO> findAllByCampusId(UUID campusId, Pageable pageable) {
        // Verifica se o campus existe
        if (!campusRepository.existsById(campusId)) {
            throw new ResourceNotFoundException("Campus não encontrado");
        }

        // Verifica permissão
        if (!SecurityUtils.isSuperAdmin() && !SecurityUtils.hasAccessToCampus(campusId)) {
            throw new SecurityException("Você não tem permissão para acessar este campus");
        }

        Page<Estacionamento> result = repository.findByCampusId(campusId, pageable);
        return result.map(EstacionamentoDTO::new);
    }

    @Transactional
    public EstacionamentoDTO insert(EstacionamentoCreateDTO dto) {
        Campus campus = campusRepository.findById(dto.getCampusId())
                .orElseThrow(() -> new ResourceNotFoundException("Campus não encontrado"));

        if (!SecurityUtils.isSuperAdmin() && !SecurityUtils.hasAccessToCampus(campus.getId())) {
            throw new SecurityException("Você não tem permissão para criar estacionamento neste campus");
        }

        Estacionamento entity = new Estacionamento();
        entity.setNome(dto.getNome());
        entity.setCapacidadeTotal(dto.getCapacidadeTotal());
        entity.setCampus(campus);

        entity = repository.save(entity);
        return new EstacionamentoDTO(entity);
    }

    @Transactional
    public EstacionamentoDTO update(UUID id, EstacionamentoUpdateDTO dto) {
        Estacionamento entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estacionamento não encontrado"));

        if (entity.getCampus() != null) {
            if (!SecurityUtils.isSuperAdmin() && !SecurityUtils.hasAccessToCampus(entity.getCampus().getId())) {
                throw new SecurityException("Você não tem permissão para modificar este estacionamento");
            }
        }

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            entity.setNome(dto.getNome());
        }

        if (dto.getCapacidadeTotal() != null) {
            entity.setCapacidadeTotal(dto.getCapacidadeTotal());
        }

        entity = repository.save(entity);
        return new EstacionamentoDTO(entity);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(UUID id) {
        Estacionamento estacionamento = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Estacionamento não encontrado"));

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

}
