package com.luis.ifpark.services;

import com.luis.ifpark.dtos.MovimentacaoDTO;
import com.luis.ifpark.entities.Estacionamento;
import com.luis.ifpark.entities.Movimentacao;
import com.luis.ifpark.entities.Usuario;
import com.luis.ifpark.entities.Veiculo;
import com.luis.ifpark.exceptions.DatabaseException;
import com.luis.ifpark.exceptions.ResourceNotFoundException;
import com.luis.ifpark.repositories.EstacionamentoRepository;
import com.luis.ifpark.repositories.MovimentacaoRepository;
import com.luis.ifpark.repositories.UsuarioRepository;
import com.luis.ifpark.repositories.VeiculoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MovimentacaoService {

    @Autowired
    private MovimentacaoRepository repository;
    
    @Autowired
    private VeiculoRepository veiculoRepository;
    
    @Autowired
    private EstacionamentoRepository estacionamentoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<MovimentacaoDTO> findAll() {
        List<Movimentacao> result = repository.findAll();
        return result.stream().map(MovimentacaoDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MovimentacaoDTO> findAll(Pageable pageable) {
        Page<Movimentacao> result = repository.findAll(pageable);
        return result.map(movimentacao -> new MovimentacaoDTO(movimentacao));
    }

    @Transactional(readOnly = true)
    public MovimentacaoDTO findById(UUID id) {
        Movimentacao movimentacaoResult = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Movimentação não encontrada")
        );
        return new MovimentacaoDTO(movimentacaoResult);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoDTO> findVeiculosNoEstacionamento(UUID estacionamentoId) {
        List<Movimentacao> result = repository.findByEstacionamentoIdAndDataSaidaIsNull(estacionamentoId);
        return result.stream().map(MovimentacaoDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public MovimentacaoDTO registrarEntrada(MovimentacaoDTO dto, UUID vigiaId) {
        Movimentacao entity = new Movimentacao();
        copyDtoToEntity(dto, entity);
        
        // Define a data de entrada como agora
        entity.setDataEntrada(LocalDateTime.now());
        
        // Define o vigia de entrada
        Usuario vigia = usuarioRepository.findById(vigiaId)
            .orElseThrow(() -> new ResourceNotFoundException("Vigia não encontrado"));
        entity.setVigiaEntrada(vigia);
        
        entity = repository.save(entity);
        return new MovimentacaoDTO(entity);
    }

    @Transactional
    public MovimentacaoDTO registrarSaida(UUID id, UUID vigiaId) {
        try {
            Movimentacao entity = repository.getReferenceById(id);
            
            // Verifica se já não tem data de saída
            if (entity.getDataSaida() != null) {
                throw new IllegalStateException("Saída já registrada para esta movimentação");
            }
            
            // Define a data de saída como agora
            entity.setDataSaida(LocalDateTime.now());
            
            // Define o vigia de saída
            Usuario vigia = usuarioRepository.findById(vigiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vigia não encontrado"));
            entity.setVigiaSaida(vigia);
            
            entity = repository.save(entity);
            return new MovimentacaoDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Movimentação não encontrada");
        }
    }

    @Transactional
    public MovimentacaoDTO update(UUID id, MovimentacaoDTO dto) {
        try {
            Movimentacao entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new MovimentacaoDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Movimentação não encontrada");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Movimentação não encontrada");
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    private void copyDtoToEntity(MovimentacaoDTO dto, Movimentacao entity) {
        if (dto.getDataEntrada() != null) {
            entity.setDataEntrada(dto.getDataEntrada());
        }
        
        if (dto.getDataSaida() != null) {
            entity.setDataSaida(dto.getDataSaida());
        }
        
        // Verifica se o veículo foi fornecido no DTO
        if (dto.getVeiculo() != null && dto.getVeiculo().getId() != null) {
            Veiculo veiculo = veiculoRepository.findById(dto.getVeiculo().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));
            entity.setVeiculo(veiculo);
        }
        
        // Verifica se o estacionamento foi fornecido no DTO
        if (dto.getEstacionamento() != null && dto.getEstacionamento().getId() != null) {
            Estacionamento estacionamento = estacionamentoRepository.findById(dto.getEstacionamento().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Estacionamento não encontrado"));
            entity.setEstacionamento(estacionamento);
        }
        
        // Verifica se o vigia de entrada foi fornecido no DTO
        if (dto.getVigiaEntrada() != null && dto.getVigiaEntrada().getId() != null) {
            Usuario vigiaEntrada = usuarioRepository.findById(dto.getVigiaEntrada().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vigia de entrada não encontrado"));
            entity.setVigiaEntrada(vigiaEntrada);
        }
        
        // Verifica se o vigia de saída foi fornecido no DTO
        if (dto.getVigiaSaida() != null && dto.getVigiaSaida().getId() != null) {
            Usuario vigiaSaida = usuarioRepository.findById(dto.getVigiaSaida().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vigia de saída não encontrado"));
            entity.setVigiaSaida(vigiaSaida);
        }
    }
}
