package com.luis.ifpark.services;

import com.luis.ifpark.dtos.usuario.UsuarioCreateDTO;
import com.luis.ifpark.dtos.usuario.UsuarioResponseDTO;
import com.luis.ifpark.dtos.usuario.UsuarioUpdateDTO;
import com.luis.ifpark.entities.Campus;
import com.luis.ifpark.entities.Pessoa;
import com.luis.ifpark.entities.Usuario;
import com.luis.ifpark.entities.enums.PapelUsuario;
import com.luis.ifpark.exceptions.EmailJaCadastradoException;
import com.luis.ifpark.exceptions.ResourceNotFoundException;
import com.luis.ifpark.repositories.CampusRepository;
import com.luis.ifpark.repositories.PessoaRepository;
import com.luis.ifpark.repositories.UsuarioRepository;
import com.luis.ifpark.utils.SecurityUtils;
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
    private CampusRepository campusRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findAll() {
        // Verificar se o usuário é SUPER_ADMIN (pode ver todos os usuários)
        if (SecurityUtils.isSuperAdmin()) {
            return usuarioRepository.findAll().stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
        }
        
        // Usuários comuns e admins só podem ver usuários do mesmo campus
        Usuario currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null && currentUser.getCampus() != null) {
            return usuarioRepository.findByCampusId(currentUser.getCampus().getId()).stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
        }
        
        // Se o usuário não tem campus associado, retorna lista vazia
        return List.of();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findById(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        
        // Verificar permissões de acesso
        checkUserAccessPermission(usuario);
        
        return toResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));
        
        // Verificar permissões de acesso
        checkUserAccessPermission(usuario);
        
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

        // Verificar permissões de criação
        checkCreatePermission(dto);
        
        // Criar o Usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha())); // Codificar a senha
        usuario.setPapel(dto.getPapel());
        usuario.setPessoa(pessoa);
        
        // Associar campus se fornecido
        if (dto.getCampusId() != null) {
            Campus campus = campusRepository.findById(dto.getCampusId())
                .orElseThrow(() -> new ResourceNotFoundException("Campus não encontrado com ID: " + dto.getCampusId()));
            usuario.setCampus(campus);
        }

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
        
        // Verificar permissões de acesso
        checkUserAccessPermission(usuario);

        // Verificar se está tentando mudar o email para um que já existe (exceto o próprio)
        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EmailJaCadastradoException("Email já cadastrado: " + dto.getEmail());
        }

        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha())); // Codificar a nova senha
        usuario.setPapel(dto.getPapel());
        
        // Atualizar campus se fornecido
        if (dto.getCampusId() != null) {
            Campus campus = campusRepository.findById(dto.getCampusId())
                .orElseThrow(() -> new ResourceNotFoundException("Campus não encontrado com ID: " + dto.getCampusId()));
            usuario.setCampus(campus);
        }

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return toResponseDTO(updatedUsuario);
    }

    @Transactional
    public void delete(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        
        // Verificar permissões de acesso
        checkUserAccessPermission(usuario);
        
        // Remover a referência do usuário na pessoa
        if (usuario.getPessoa() != null) {
            usuario.getPessoa().setUsuario(null);
            pessoaRepository.save(usuario.getPessoa());
        }
        
        usuarioRepository.deleteById(id);
    }
    
    /**
     * Verifica se o usuário atual tem permissão para acessar o usuário especificado
     */
    private void checkUserAccessPermission(Usuario targetUser) {
        // SUPER_ADMIN tem acesso a tudo
        if (SecurityUtils.isSuperAdmin()) {
            return;
        }
        
        Usuario currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Usuário não autenticado");
        }
        
        // ADMIN pode acessar usuários do mesmo campus
        if (currentUser.getPapel() == PapelUsuario.ROLE_ADMIN) {
            if (currentUser.getCampus() == null || targetUser.getCampus() == null) {
                throw new SecurityException("Acesso negado: Usuário ou targetUser sem campus associado");
            }
            
            if (!currentUser.getCampus().getId().equals(targetUser.getCampus().getId())) {
                throw new SecurityException("Acesso negado: Você só pode acessar usuários do seu campus");
            }
            return;
        }
        
        // VIGIA e COMUM só podem acessar a si mesmos
        if (!currentUser.getId().equals(targetUser.getId())) {
            throw new SecurityException("Acesso negado: Você só pode acessar seu próprio usuário");
        }
    }
    
    /**
     * Verifica se o usuário atual tem permissão para criar um usuário com as permissões especificadas
     */
    private void checkCreatePermission(UsuarioCreateDTO dto) {
        // SUPER_ADMIN pode criar qualquer tipo de usuário
        if (SecurityUtils.isSuperAdmin()) {
            return;
        }
        
        Usuario currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new SecurityException("Usuário não autenticado");
        }
        
        // ADMIN só pode criar usuários do mesmo campus
        if (currentUser.getPapel() == PapelUsuario.ROLE_ADMIN) {
            if (currentUser.getCampus() == null) {
                throw new SecurityException("Acesso negado: ADMIN sem campus associado");
            }
            
            if (dto.getCampusId() == null || !dto.getCampusId().equals(currentUser.getCampus().getId())) {
                throw new SecurityException("Acesso negado: ADMIN só pode criar usuários no seu campus");
            }
            
            // ADMIN não pode criar outro ADMIN ou SUPER_ADMIN
            if (dto.getPapel() == PapelUsuario.ROLE_ADMIN || dto.getPapel() == PapelUsuario.ROLE_SUPER_ADMIN) {
                throw new SecurityException("Acesso negado: ADMIN não pode criar usuários com papel ADMIN ou SUPER_ADMIN");
            }
            return;
        }
        
        // VIGIA e COMUM não podem criar usuários
        throw new SecurityException("Acesso negado: Você não tem permissão para criar usuários");
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
        
        if (usuario.getCampus() != null) {
            // Criar um DTO de campus simplificado
            dto.setCampus(new com.luis.ifpark.dtos.campus.CampusDTO(
                usuario.getCampus().getId(),
                usuario.getCampus().getNome(),
                null // Endereço será carregado se necessário
            ));
        }
        
        return dto;
    }
}
