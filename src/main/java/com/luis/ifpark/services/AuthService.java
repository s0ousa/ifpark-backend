package com.luis.ifpark.services;

import com.luis.ifpark.dtos.auth.RegistroCompletoDTO;
import com.luis.ifpark.dtos.usuario.UsuarioResponseDTO;
import com.luis.ifpark.entities.Pessoa;
import com.luis.ifpark.entities.Usuario;
import com.luis.ifpark.entities.enums.PapelUsuario;
import com.luis.ifpark.entities.enums.StatusPessoa;
import com.luis.ifpark.entities.enums.TipoPessoa;
import com.luis.ifpark.exceptions.CpfJaCadastradoException;
import com.luis.ifpark.exceptions.EmailJaCadastradoException;
import com.luis.ifpark.exceptions.ResourceNotFoundException;
import com.luis.ifpark.repositories.PessoaRepository;
import com.luis.ifpark.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO register(RegistroCompletoDTO dto) {
        // Verificar se CPF já existe
        if (pessoaRepository.existsByCpf(dto.getCpf())) {
            throw new CpfJaCadastradoException("CPF já cadastrado: " + dto.getCpf());
        }

        // Verificar se email já existe
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EmailJaCadastradoException("Email já cadastrado: " + dto.getEmail());
        }

        // Validar matrícula para alunos
        if (dto.getTipo() == TipoPessoa.ALUNO && (dto.getMatricula() == null || dto.getMatricula().isEmpty())) {
            throw new IllegalArgumentException("Matrícula é obrigatória para alunos");
        }

        // Criar a Pessoa
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.getNome());
        pessoa.setCpf(dto.getCpf());
        pessoa.setMatricula(dto.getMatricula());
        pessoa.setTipo(dto.getTipo());
        pessoa.setStatus(StatusPessoa.ATIVO); // Pessoas criadas via registro começam ativas
        pessoa.setTelefone(dto.getTelefone());

        Pessoa savedPessoa = pessoaRepository.save(pessoa);

        // Criar o Usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha())); // Codificar a senha
        usuario.setPapel(PapelUsuario.ROLE_COMUM); // Por padrão, usuários comuns
        usuario.setPessoa(savedPessoa);

        Usuario savedUsuario = usuarioRepository.save(usuario);

        // Atualizar a pessoa com a referência ao usuário
        savedPessoa.setUsuario(savedUsuario);
        pessoaRepository.save(savedPessoa);

        return toResponseDTO(savedUsuario);
    }

    @Transactional
    public UsuarioResponseDTO createUsuarioForExistingPessoa(String cpf, String email, String senha, PapelUsuario papel) {
        // Verificar se email já existe
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException("Email já cadastrado: " + email);
        }

        // Encontrar a pessoa pelo CPF
        Pessoa pessoa = pessoaRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com CPF: " + cpf));

        // Verificar se a pessoa já tem um usuário
        if (pessoa.getUsuario() != null) {
            throw new IllegalArgumentException("Pessoa já possui um usuário associado");
        }

        // Criar o Usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha)); // Codificar a senha
        usuario.setPapel(papel);
        usuario.setPessoa(pessoa);

        Usuario savedUsuario = usuarioRepository.save(usuario);

        // Atualizar a pessoa com a referência ao usuário
        pessoa.setUsuario(savedUsuario);
        pessoaRepository.save(pessoa);

        return toResponseDTO(savedUsuario);
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setPapel(usuario.getPapel());
        
        if (usuario.getPessoa() != null) {
            // Criar um DTO de pessoa simplificado
            dto.setPessoa(new com.luis.ifpark.dtos.pessoa.PessoaResponseDTO(
                usuario.getPessoa().getId(),
                usuario.getPessoa().getNome(),
                usuario.getPessoa().getCpf(),
                usuario.getPessoa().getMatricula(),
                usuario.getPessoa().getTipo(),
                usuario.getPessoa().getStatus(),
                usuario.getPessoa().getTelefone()
            ));
        }
        
        return dto;
    }
}
