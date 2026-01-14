package com.luis.ifpark.services;

import com.luis.ifpark.dtos.pessoa.*;
import com.luis.ifpark.dtos.veiculo.VeiculoResumoDTO;
import com.luis.ifpark.entities.Endereco;
import com.luis.ifpark.entities.Pessoa;
import com.luis.ifpark.entities.Veiculo;
import com.luis.ifpark.entities.enums.StatusAprovacao;
import com.luis.ifpark.entities.enums.StatusPessoa;
import com.luis.ifpark.entities.enums.TipoPessoa;
import com.luis.ifpark.exceptions.CpfJaCadastradoException;
import com.luis.ifpark.exceptions.RegraDeNegocioException;
import com.luis.ifpark.exceptions.ResourceNotFoundException;
import com.luis.ifpark.repositories.EnderecoRepository;
import com.luis.ifpark.repositories.PessoaRepository;
import com.luis.ifpark.repositories.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Transactional(readOnly = true)
    public List<PessoaResponseDTO> findAll() {
        return pessoaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PessoaResponseDTO> findAll(Pageable pageable) {
        return pessoaRepository.findAll(pageable).map(this::toResponseDTO);
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
    public PessoaResponseDTO createVisitor(VisitanteDTO dto) {
        if (pessoaRepository.existsByCpf(dto.getCpf())) {
            throw new CpfJaCadastradoException("CPF já cadastrado: " + dto.getCpf());
        }

        if (dto.getTipo() != TipoPessoa.VISITANTE) {
            throw new RegraDeNegocioException("O tipo deve ser VISITANTE para este endpoint");
        }

        if (veiculoRepository.existsByPlaca(dto.getPlaca())) {
            throw new RegraDeNegocioException("Placa já cadastrada: " + dto.getPlaca());
        }

        Endereco endereco = new Endereco();
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setCep(dto.getCep());
        Endereco savedEndereco = enderecoRepository.save(endereco);

        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.getNome());
        pessoa.setCpf(dto.getCpf());
        pessoa.setMatricula(null);
        pessoa.setTipo(TipoPessoa.VISITANTE);
        pessoa.setStatus(StatusPessoa.ATIVO);
        pessoa.setTelefone(dto.getTelefone());
        pessoa.setEndereco(savedEndereco);
        pessoa.setUsuario(null);

        Pessoa savedPessoa = pessoaRepository.save(pessoa);

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(dto.getPlaca().toUpperCase());
        veiculo.setModelo(dto.getModelo());
        veiculo.setStatusAprovacao(StatusAprovacao.APROVADO);
        veiculo.setMotivoRejeicao(null);
        veiculo.setPessoa(savedPessoa);

        veiculoRepository.save(veiculo);

        return new PessoaResponseDTO(savedPessoa);
    }

    @Transactional
    public PessoaResponseDTO update(UUID id, PessoaUpdateDTO dto) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com ID: " + id));

        if (dto.getNome() != null) {
            pessoa.setNome(dto.getNome());
        }

        if (dto.getCpf() != null) {
            if (!pessoa.getCpf().equals(dto.getCpf()) && pessoaRepository.existsByCpf(dto.getCpf())) {
                throw new CpfJaCadastradoException("CPF já cadastrado: " + dto.getCpf());
            }
            pessoa.setCpf(dto.getCpf());
        }

        if (dto.getTipo() != null) {
            pessoa.setTipo(dto.getTipo());
        }

        if (dto.getMatricula() != null) {
            pessoa.setMatricula(dto.getMatricula());
        }

        if (pessoa.getTipo() == TipoPessoa.ALUNO) {
            boolean matriculaVazia = pessoa.getMatricula() == null || pessoa.getMatricula().isEmpty();
            if (matriculaVazia) {
                throw new IllegalArgumentException("Matrícula é obrigatória para alunos");
            }
        }

        if (dto.getStatus() != null) {
            pessoa.setStatus(dto.getStatus());
        }

        if (dto.getTelefone() != null) {
            pessoa.setTelefone(dto.getTelefone());
        }

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

    @Transactional
    public PessoaResponseDTO atualizarStatus(UUID id, StatusPessoa novoStatus) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada"));

        pessoa.setStatus(novoStatus);
        pessoa = pessoaRepository.save(pessoa);

        return new PessoaResponseDTO(pessoa);
    }

    @Transactional(readOnly = true)
    public Page<MotoristaResponseDTO> findAllDrivers(Pageable pageable) {
        // Passo 1: Buscar a página de IDs
        Page<UUID> pageIds = pessoaRepository.findIdsMotoristas(pageable);

        if (pageIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // Passo 2: Buscar as entidades completas (com veículos) usando os IDs
        List<UUID> ids = pageIds.getContent();
        List<Pessoa> pessoasComVeiculos = pessoaRepository.findPessoasComVeiculosPorIds(ids);

        // Passo 3: Converter para o seu DTO
        List<MotoristaResponseDTO> dtos = pessoasComVeiculos.stream()
                .map(this::toMotoristaDTO)
                .collect(Collectors.toList());

        // Passo 4: Retornar o PageImpl mantendo os metadados da paginação original
        return new PageImpl<>(dtos, pageable, pageIds.getTotalElements());
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

    private MotoristaResponseDTO toMotoristaDTO(Pessoa pessoa) {
        List<VeiculoResumoDTO> veiculosDTO = pessoa.getVeiculos().stream()
                .map(v -> new VeiculoResumoDTO(
                        v.getId(),
                        v.getPlaca(),
                        v.getModelo(),
                        v.getStatusAprovacao()
                ))
                .collect(Collectors.toList());

        // Retorna o DTO preenchido
        return new MotoristaResponseDTO(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getCpf(),
                pessoa.getTipo(),
                pessoa.getTelefone(),
                pessoa.getStatus(),
                veiculosDTO
        );
    }
}
