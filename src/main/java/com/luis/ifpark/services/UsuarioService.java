package com.luis.ifpark.services;

import com.luis.ifpark.dtos.usuario.UsuarioCreateDTO;
import com.luis.ifpark.dtos.usuario.UsuarioResponseDTO;
import com.luis.ifpark.dtos.usuario.UsuarioUpdateDTO;
import com.luis.ifpark.entities.Pessoa;
import com.luis.ifpark.entities.Usuario;
import com.luis.ifpark.exceptions.EmailJaCadastradoException;
import com.luis.ifpark.exceptions.ResourceNotFoundException;
import com.luis.ifpark.repositories.PessoaRepository;
import com.luis.ifpark.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findById(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        return toResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));
        return toResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO create(UsuarioCreateDTO dto) {
        // Verificar se email já existe
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EmailJaCadastradoException("Email já cadastrado: " + dto.getEmail());
        }

        // Encontrar a pessoa pelo ID
        Pessoa pessoa = pessoaRepository.findById(dto.getPessoaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada com ID: " + dto.getPessoaId()));

        // Verificar se a pessoa já tem um usuário
        if (pessoa.getUsuario() != null) {
            throw new IllegalArgumentException("Pessoa já possui um usuário associado");
        }

        // Criar o Usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha())); // Codificar a senha
        usuario.setPapel(dto.getPapel());
        usuario.setPessoa(pessoa);

        Usuario savedUsuario = usuarioRepository.save(usuario);

        // Atualizar a pessoa com a referência ao usuário
        pessoa.setUsuario(savedUsuario);
        pessoaRepository.save(pessoa);

        return toResponseDTO(savedUsuario);
    }

    @Transactional
    public UsuarioResponseDTO update(UUID id, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        // Verificar se está tentando mudar o email para um que já existe (exceto o próprio)
        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EmailJaCadastradoException("Email já cadastrado: " + dto.getEmail());
        }

        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha())); // Codificar a nova senha
        usuario.setPapel(dto.getPapel());

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return toResponseDTO(updatedUsuario);
    }

    @Transactional
    public void delete(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        
        // Remover a referência do usuário na pessoa
        if (usuario.getPessoa() != null) {
            usuario.getPessoa().setUsuario(null);
            pessoaRepository.save(usuario.getPessoa());
        }
        
        usuarioRepository.deleteById(id);
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
