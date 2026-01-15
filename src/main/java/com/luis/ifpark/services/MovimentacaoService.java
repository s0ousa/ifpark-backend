package com.luis.ifpark.services;

import com.luis.ifpark.dtos.movimentacao.MovimentacaoDTO;
import com.luis.ifpark.dtos.movimentacao.EntryRegisterDTO;
import com.luis.ifpark.dtos.movimentacao.ExitRegisterDTO;
import com.luis.ifpark.dtos.movimentacao.MovimentacaoResponseDTO;
import com.luis.ifpark.entities.Estacionamento;
import com.luis.ifpark.entities.Movimentacao;
import com.luis.ifpark.entities.Usuario;
import com.luis.ifpark.entities.Veiculo;
import com.luis.ifpark.entities.enums.StatusAprovacao;
import com.luis.ifpark.exceptions.DatabaseException;
import com.luis.ifpark.exceptions.RegraDeNegocioException;
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
import org.springframework.security.core.context.SecurityContextHolder;
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
    public Page<MovimentacaoDTO> findByUsuario(UUID usuarioId, UUID estacionamentoId, Pageable pageable) {
        Page<Movimentacao> result;

        if (estacionamentoId != null) {
            result = repository.findByUsuarioIdAndEstacionamentoId(usuarioId, estacionamentoId, pageable);
        } else {
            result = repository.findByUsuarioId(usuarioId, pageable);
        }

        return result.map(MovimentacaoDTO::new);
    }

  @Transactional(readOnly = true)
    public Page<MovimentacaoDTO> findAll(UUID estacionamentoId, Pageable pageable) {
        Page<Movimentacao> result;

        if (estacionamentoId != null) {
            result = repository.findByEstacionamentoId(estacionamentoId, pageable);
        } else {
            result = repository.findAll(pageable);
        }

        return result.map(MovimentacaoDTO::new);
    }

    @Transactional(readOnly = true)
    public MovimentacaoDTO findById(UUID id) {
        Movimentacao movimentacaoResult = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Movimentação não encontrada")
        );
        return new MovimentacaoDTO(movimentacaoResult);
    }


    @Transactional(readOnly = true)
    public Page<MovimentacaoDTO> findVeiculosNoEstacionamento(UUID estacionamentoId, Pageable pageable) {
        Page<Movimentacao> result = repository.findByEstacionamentoIdAndDataSaidaIsNull(estacionamentoId, pageable);
        return result.map(MovimentacaoDTO::new);
    }

    @Transactional
    public MovimentacaoResponseDTO registrarEntrada(EntryRegisterDTO dto) {
        String emailVigia = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario vigia = usuarioRepository.findByEmail(emailVigia)
                .orElseThrow(() -> new ResourceNotFoundException("Vigia logado não encontrado no banco."));

        Veiculo veiculo = veiculoRepository.findByPlacaIgnoreCase(dto.getPlaca())
                .orElseThrow(() -> new RegraDeNegocioException("Veículo não encontrado."));

        Estacionamento estacionamento = estacionamentoRepository.findByIdWithLock(dto.getEstacionamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estacionamento não encontrado."));

        if (!estacionamento.getAtivo()) {
            throw new RegraDeNegocioException("Não é possível registrar entrada em um estacionamento inativo");
        }

        if (veiculo.getStatusAprovacao() != StatusAprovacao.APROVADO) {
            throw new RegraDeNegocioException("Veículo não aprovado.");
        }
        if (repository.existsByVeiculoAndDataSaidaIsNull(veiculo)) {
            throw new RegraDeNegocioException("Veículo já está dentro de um estacionamento.");
        }
        long ocupadas = repository.countByEstacionamentoAndDataSaidaIsNull(estacionamento);
        if (ocupadas >= estacionamento.getCapacidadeTotal()) {
            throw new RegraDeNegocioException("Estacionamento lotado.");
        }

        Movimentacao mov = new Movimentacao();
        mov.setVeiculo(veiculo);
        mov.setEstacionamento(estacionamento);
        mov.setVigiaEntrada(vigia);
        mov.setDataEntrada(LocalDateTime.now());

        return converterParaDTO(repository.save(mov));
    }

    @Transactional
    public MovimentacaoResponseDTO registrarSaida(ExitRegisterDTO dto) {
        Veiculo veiculo = veiculoRepository.findByPlacaIgnoreCase(dto.getPlaca())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));

        String emailVigia = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario vigia = usuarioRepository.findByEmail(emailVigia)
                .orElseThrow(() -> new ResourceNotFoundException("Vigia logado não encontrado no banco."));

        Movimentacao mov = repository.findFirstByVeiculoAndDataSaidaIsNull(veiculo)
                .orElseThrow(() -> new RegraDeNegocioException("Veículo não está estacionado."));

        mov.setDataSaida(LocalDateTime.now());
        mov.setVigiaSaida(vigia);

        return converterParaDTO(repository.save(mov));
    }

    private MovimentacaoResponseDTO converterParaDTO(Movimentacao m) {
        MovimentacaoResponseDTO dto = new MovimentacaoResponseDTO();
        dto.setId(m.getId());
        dto.setPlaca(m.getVeiculo().getPlaca());
        dto.setModelo(m.getVeiculo().getModelo());

        dto.setEstacionamentoId(m.getEstacionamento().getId());
        dto.setNomeEstacionamento(m.getEstacionamento().getNome());

        dto.setDataEntrada(m.getDataEntrada());
        dto.setDataSaida(m.getDataSaida());

        dto.setVigiaEntradaId(m.getVigiaEntrada().getId());
        dto.setNomeVigiaEntrada(m.getVigiaEntrada().getPessoa().getNome());

        if (m.getVigiaSaida() != null) {
            dto.setVigiaSaidaId(m.getVigiaSaida().getId());
            dto.setNomeVigiaSaida(m.getVigiaSaida().getPessoa().getNome());
        }
        return dto;
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
