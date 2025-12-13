package com.luis.ifpark.services;

import com.luis.ifpark.dtos.pessoa.PessoaCreateDTO;
import com.luis.ifpark.dtos.pessoa.PessoaResponseDTO;
import com.luis.ifpark.dtos.pessoa.PessoaUpdateDTO;
import com.luis.ifpark.entities.Pessoa;
import com.luis.ifpark.entities.enums.StatusPessoa;
import com.luis.ifpark.entities.enums.TipoPessoa;
import com.luis.ifpark.exceptions.CpfJaCadastradoException;
import com.luis.ifpark.exceptions.ResourceNotFoundException;
import com.luis.ifpark.repositories.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Transactional(readOnly = true)
    public List<PessoaResponseDTO> findAll() {
        return pessoaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PessoaResponseDTO findById(UUID id) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com ID: " + id));
        return toResponseDTO(pessoa);
    }

    @Transactional(readOnly = true)
    public PessoaResponseDTO findByCpf(String cpf) {
        Pessoa pessoa = pessoaRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com CPF: " + cpf));
        return toResponseDTO(pessoa);
    }

    @Transactional
    public PessoaResponseDTO create(PessoaCreateDTO dto) {
        // Verificar se CPF já existe
        if (pessoaRepository.existsByCpf(dto.getCpf())) {
            throw new CpfJaCadastradoException("CPF já cadastrado: " + dto.getCpf());
        }

        // Validar matrícula para alunos
        if (dto.getTipo() == TipoPessoa.ALUNO && (dto.getMatricula() == null || dto.getMatricula().isEmpty())) {
            throw new IllegalArgumentException("Matrícula é obrigatória para alunos");
        }

        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.getNome());
        pessoa.setCpf(dto.getCpf());
        pessoa.setMatricula(dto.getMatricula());
        pessoa.setTipo(dto.getTipo());
        pessoa.setStatus(dto.getStatus());
        pessoa.setTelefone(dto.getTelefone());

        Pessoa savedPessoa = pessoaRepository.save(pessoa);
        return toResponseDTO(savedPessoa);
    }

    @Transactional
    public PessoaResponseDTO update(UUID id, PessoaUpdateDTO dto) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com ID: " + id));

        // Verificar se está tentando mudar o CPF para um que já existe (exceto o próprio)
        if (!pessoa.getCpf().equals(dto.getCpf()) && pessoaRepository.existsByCpf(dto.getCpf())) {
            throw new CpfJaCadastradoException("CPF já cadastrado: " + dto.getCpf());
        }

        // Validar matrícula para alunos
        if (dto.getTipo() == TipoPessoa.ALUNO && (dto.getMatricula() == null || dto.getMatricula().isEmpty())) {
            throw new IllegalArgumentException("Matrícula é obrigatória para alunos");
        }

        pessoa.setNome(dto.getNome());
        pessoa.setCpf(dto.getCpf());
        pessoa.setMatricula(dto.getMatricula());
        pessoa.setTipo(dto.getTipo());
        pessoa.setStatus(dto.getStatus());
        pessoa.setTelefone(dto.getTelefone());

        Pessoa updatedPessoa = pessoaRepository.save(pessoa);
        return toResponseDTO(updatedPessoa);
    }

    @Transactional
    public void delete(UUID id) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com ID: " + id));
        
        // Em vez de deletar, vamos inativar a pessoa
        pessoa.setStatus(StatusPessoa.INATIVO);
        pessoaRepository.save(pessoa);
    }

    private PessoaResponseDTO toResponseDTO(Pessoa pessoa) {
        PessoaResponseDTO dto = new PessoaResponseDTO();
        dto.setId(pessoa.getId());
        dto.setNome(pessoa.getNome());
        dto.setCpf(pessoa.getCpf());
        dto.setMatricula(pessoa.getMatricula());
        dto.setTipo(pessoa.getTipo());
        dto.setStatus(pessoa.getStatus());
        dto.setTelefone(pessoa.getTelefone());
        return dto;
    }
}
