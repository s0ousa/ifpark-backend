package com.luis.ifpark.services;

import com.luis.ifpark.dtos.veiculo.VeiculoCreateDTO;
import com.luis.ifpark.dtos.veiculo.VeiculoDTO;
import com.luis.ifpark.entities.Pessoa;
import com.luis.ifpark.entities.Usuario;
import com.luis.ifpark.entities.Veiculo;
import com.luis.ifpark.entities.enums.StatusAprovacao;
import com.luis.ifpark.exceptions.DatabaseException;
import com.luis.ifpark.exceptions.ResourceNotFoundException;
import com.luis.ifpark.repositories.PessoaRepository;
import com.luis.ifpark.repositories.VeiculoRepository;
import com.luis.ifpark.utils.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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
    public Page<VeiculoDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(VeiculoDTO::new);
    }

    @Transactional(readOnly = true)
    public List<VeiculoDTO> findByCampus(UUID campusId) {
        if (!SecurityUtils.isSuperAdmin() && !SecurityUtils.hasAccessToCampus(campusId)) {
            throw new org.springframework.security.access.
                    AccessDeniedException("Você não tem permissão para visualizar veículos deste campus.");
        }

        List<Veiculo> list = repository.findByCampusId(campusId);
        return list.stream().map(VeiculoDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<VeiculoDTO> findByCampus(UUID campusId, Pageable pageable) {
        if (!SecurityUtils.isSuperAdmin() && !SecurityUtils.hasAccessToCampus(campusId)) {
            throw new org.springframework.security.access.
                    AccessDeniedException("Você não tem permissão para visualizar veículos deste campus.");
        }

        return repository.findByCampusId(campusId, pageable).map(VeiculoDTO::new);
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

    @Transactional(readOnly = true)
    public Page<VeiculoDTO> findByPessoaId(UUID pessoaId, Pageable pageable) {
        return repository.findByPessoaId(pessoaId, pageable).map(VeiculoDTO::new);
    }

    @Transactional(readOnly = true)
    public VeiculoDTO findByPlaca(String placa) {
        Veiculo veiculo = repository.findByPlacaIgnoreCase(placa)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado com a placa: " + placa));
        return new VeiculoDTO(veiculo);
    }

    @Transactional
    public VeiculoDTO insert(VeiculoCreateDTO dto) {
        Pessoa pessoa = pessoaRepository.findById(dto.getPessoaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada"));

        if (repository.existsByPlaca(dto.getPlaca().toUpperCase())) {
            throw new DataIntegrityViolationException("Placa já cadastrada");
        }

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(dto.getPlaca().toUpperCase());
        veiculo.setModelo(dto.getModelo());
        veiculo.setStatusAprovacao(StatusAprovacao.PENDENTE);
        veiculo.setPessoa(pessoa);

        veiculo = repository.save(veiculo);

        return new VeiculoDTO(veiculo);
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

            validarAcessoAoVeiculo(entity);

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

            validarAcessoAoVeiculo(entity);

            entity.setStatusAprovacao(StatusAprovacao.REJEITADO);
            entity.setMotivoRejeicao(motivo);
            entity = repository.save(entity);
            return new VeiculoDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Veículo não encontrado");
        }
    }

    private void validarAcessoAoVeiculo(Veiculo veiculo) {
        if (SecurityUtils.isSuperAdmin()) {
            return;
        }

        Usuario adminLogado = SecurityUtils.getCurrentUser();

        Pessoa donoVeiculo = veiculo.getPessoa();
        Usuario usuarioDono = donoVeiculo.getUsuario();

        if (usuarioDono == null || usuarioDono.getCampus() == null) {
            throw new AccessDeniedException("O proprietário deste veículo não possui vínculo com campus. Apenas Super Admin pode gerenciar.");
        }

        if (adminLogado.getCampus() == null) {
            throw new AccessDeniedException("Você não está vinculado a nenhum campus para realizar esta ação.");
        }

        if (!adminLogado.getCampus().getId().equals(usuarioDono.getCampus().getId())) {
            throw new AccessDeniedException("Você não tem permissão para gerenciar veículos de outro campus.");
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
