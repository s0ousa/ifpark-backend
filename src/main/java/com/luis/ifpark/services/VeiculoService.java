package com.luis.ifpark.services;

import com.luis.ifpark.dtos.VeiculoDTO;
import com.luis.ifpark.entities.Pessoa;
import com.luis.ifpark.entities.Veiculo;
import com.luis.ifpark.entities.enums.StatusAprovacao;
import com.luis.ifpark.exceptions.DatabaseException;
import com.luis.ifpark.exceptions.ResourceNotFoundException;
import com.luis.ifpark.repositories.PessoaRepository;
import com.luis.ifpark.repositories.VeiculoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository repository;
    
    @Autowired
    private PessoaRepository pessoaRepository;

    @Transactional(readOnly = true)
    public List<VeiculoDTO> findAll() {
        List<Veiculo> result = repository.findAll();
        return result.stream().map(VeiculoDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VeiculoDTO findById(UUID id) {
        Veiculo veiculoResult = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Veículo não encontrado")
        );
        return new VeiculoDTO(veiculoResult);
    }

    @Transactional(readOnly = true)
    public List<VeiculoDTO> findByPessoaId(UUID pessoaId) {
        List<Veiculo> result = repository.findByPessoaId(pessoaId);
        return result.stream().map(VeiculoDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public VeiculoDTO insert(VeiculoDTO dto) {
        Veiculo entity = new Veiculo();
        copyDtoToEntity(dto, entity);
        entity.setStatusAprovacao(StatusAprovacao.PENDENTE); // Novos veículos começam como pendentes
        entity = repository.save(entity);
        return new VeiculoDTO(entity);
    }

    @Transactional
    public VeiculoDTO update(UUID id, VeiculoDTO dto) {
        try {
            Veiculo entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new VeiculoDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Veículo não encontrado");
        }
    }

    @Transactional
    public VeiculoDTO aprovarVeiculo(UUID id) {
        try {
            Veiculo entity = repository.getReferenceById(id);
            entity.setStatusAprovacao(StatusAprovacao.APROVADO);
            entity = repository.save(entity);
            return new VeiculoDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Veículo não encontrado");
        }
    }

    @Transactional
    public VeiculoDTO rejeitarVeiculo(UUID id, String motivo) {
        try {
            Veiculo entity = repository.getReferenceById(id);
            entity.setStatusAprovacao(StatusAprovacao.REJEITADO);
            entity.setMotivoRejeicao(motivo);
            entity = repository.save(entity);
            return new VeiculoDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Veículo não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Veículo não encontrado");
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    private void copyDtoToEntity(VeiculoDTO dto, Veiculo entity) {
        entity.setPlaca(dto.getPlaca());
        entity.setModelo(dto.getModelo());
        
        if (dto.getStatusAprovacao() != null) {
            entity.setStatusAprovacao(dto.getStatusAprovacao());
        }
        
        if (dto.getMotivoRejeicao() != null) {
            entity.setMotivoRejeicao(dto.getMotivoRejeicao());
        }
        
        // Verifica se a pessoa foi fornecida no DTO
        if (dto.getPessoa() != null && dto.getPessoa().getId() != null) {
            Pessoa pessoa = pessoaRepository.findById(dto.getPessoa().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada"));
            entity.setPessoa(pessoa);
        }
    }
}
