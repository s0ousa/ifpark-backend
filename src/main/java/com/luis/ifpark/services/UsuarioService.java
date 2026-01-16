package com.luis.ifpark.services;

import com.luis.ifpark.dtos.auth.RegistroCompletoDTO;
import com.luis.ifpark.dtos.usuario.UsuarioCreateDTO;
import com.luis.ifpark.dtos.usuario.UsuarioResponseDTO;
import com.luis.ifpark.dtos.usuario.UsuarioUpdateDTO;
import com.luis.ifpark.entities.Campus;
import com.luis.ifpark.entities.Endereco;
import com.luis.ifpark.entities.Pessoa;
import com.luis.ifpark.entities.Usuario;
import com.luis.ifpark.entities.enums.PapelUsuario;
import com.luis.ifpark.entities.enums.StatusPessoa;
import com.luis.ifpark.entities.enums.TipoPessoa;
import com.luis.ifpark.exceptions.CpfJaCadastradoException;
import com.luis.ifpark.exceptions.EmailJaCadastradoException;
import com.luis.ifpark.exceptions.RegraDeNegocioException;
import com.luis.ifpark.exceptions.ResourceNotFoundException;
import com.luis.ifpark.repositories.CampusRepository;
import com.luis.ifpark.repositories.EnderecoRepository;
import com.luis.ifpark.repositories.PessoaRepository;
import com.luis.ifpark.repositories.UsuarioRepository;
import com.luis.ifpark.utils.SecurityUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private EnderecoRepository enderecoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findAll(String papelStr) {

        Specification<Usuario> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (papelStr != null && !papelStr.isEmpty()) {
                try {
                    PapelUsuario papelEnum = PapelUsuario.valueOf(papelStr.toUpperCase());
                    predicates.add(cb.equal(root.get("papel"), papelEnum));
                } catch (IllegalArgumentException e) {
                    predicates.add(cb.disjunction());
                }
            }

            if (!SecurityUtils.isSuperAdmin()) {
                Usuario currentUser = SecurityUtils.getCurrentUser();

                if (currentUser != null && currentUser.getCampus() != null) {
                    predicates.add(cb.equal(root.get("campus").get("id"), currentUser.getCampus().getId()));
                } else {
                    predicates.add(cb.disjunction());
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return usuarioRepository.findAll(spec).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> findAll(String papelStr, Pageable pageable) {

        Specification<Usuario> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (papelStr != null && !papelStr.isEmpty()) {
                try {
                    PapelUsuario papelEnum = PapelUsuario.valueOf(papelStr.toUpperCase());
                    predicates.add(cb.equal(root.get("papel"), papelEnum));
                } catch (IllegalArgumentException e) {
                    predicates.add(cb.disjunction());
                }
            }

            if (!SecurityUtils.isSuperAdmin()) {
                Usuario currentUser = SecurityUtils.getCurrentUser();

                if (currentUser != null && currentUser.getCampus() != null) {
                    predicates.add(cb.equal(root.get("campus").get("id"), currentUser.getCampus().getId()));
                } else {
                    predicates.add(cb.disjunction());
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return usuarioRepository.findAll(spec, pageable).map(this::toResponseDTO);
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
    public UsuarioResponseDTO create(RegistroCompletoDTO dto) {
        checkCreatePermission(dto);

        if (pessoaRepository.existsByCpf(dto.getCpf())) {
            throw new CpfJaCadastradoException("CPF já cadastrado: " + dto.getCpf());
        }

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EmailJaCadastradoException("Email já cadastrado: " + dto.getEmail());
        }

        if (dto.getTipo() == TipoPessoa.ALUNO && (dto.getMatricula() == null || dto.getMatricula().isEmpty())) {
            throw new IllegalArgumentException("Matrícula é obrigatória para alunos");
        }

        if (dto.getPapel()== null) {
            throw new RegraDeNegocioException("Papel é obrigatório para usuários");
        }

        Campus campus = campusRepository.findById(dto.getCampusId())
                .orElseThrow(() -> new ResourceNotFoundException("Campus não encontrado"));

        if (!campus.getAtivo()) {
            throw new RegraDeNegocioException("Não é possível criar usuário em um campus inativo");
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
        pessoa.setMatricula(dto.getMatricula());
        pessoa.setTipo(dto.getTipo());
        pessoa.setStatus(StatusPessoa.ATIVO);
        pessoa.setTelefone(dto.getTelefone());
        pessoa.setEndereco(savedEndereco);

        Pessoa savedPessoa = pessoaRepository.save(pessoa);

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha())); // Codificar a senha
        usuario.setPapel(dto.getPapel());
        usuario.setPessoa(pessoa);
        usuario.setCampus(campus);

        Usuario savedUsuario = usuarioRepository.save(usuario);

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

        // Atualizar dados do usuário
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            // Verificar se está tentando mudar o email para um que já existe (exceto o próprio)
            if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new EmailJaCadastradoException("Email já cadastrado: " + dto.getEmail());
            }
            usuario.setEmail(dto.getEmail());
        }

        if (dto.getPapel() != null) {
            // ADMIN não pode promover usuários a SUPER_ADMIN
            if (!SecurityUtils.isSuperAdmin() && dto.getPapel() == PapelUsuario.ROLE_SUPER_ADMIN) {
                throw new AccessDeniedException("Acesso negado: Você não pode promover usuários a SUPER_ADMIN");
            }
            usuario.setPapel(dto.getPapel());
        }

        // Atualizar senha se fornecida
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        
        // Atualizar campus se fornecido (apenas SUPER_ADMIN pode alterar)
        if (dto.getCampusId() != null) {
            if (!SecurityUtils.isSuperAdmin()) {
                throw new AccessDeniedException("Acesso negado: Apenas SUPER_ADMIN pode alterar o campus do usuário");
            }
            Campus campus = campusRepository.findById(dto.getCampusId())
                .orElseThrow(() -> new ResourceNotFoundException("Campus não encontrado com ID: " + dto.getCampusId()));
            usuario.setCampus(campus);
        }

        // Atualizar dados da pessoa
        if (usuario.getPessoa() != null) {
            Pessoa pessoa = usuario.getPessoa();
            boolean pessoaUpdated = false;

            if (dto.getNome() != null && !dto.getNome().isEmpty()) {
                pessoa.setNome(dto.getNome());
                pessoaUpdated = true;
            }

            if (dto.getCpf() != null && !dto.getCpf().isEmpty()) {
                // Verificar se o CPF já existe (exceto para a própria pessoa)
                if (!pessoa.getCpf().equals(dto.getCpf()) && pessoaRepository.existsByCpf(dto.getCpf())) {
                    throw new CpfJaCadastradoException("CPF já cadastrado: " + dto.getCpf());
                }
                pessoa.setCpf(dto.getCpf());
                pessoaUpdated = true;
            }

            if (dto.getMatricula() != null) {
                pessoa.setMatricula(dto.getMatricula());
                pessoaUpdated = true;
            }

            if (dto.getTipo() != null) {
                // Validar matrícula obrigatória para alunos
                if (dto.getTipo() == TipoPessoa.ALUNO && (pessoa.getMatricula() == null || pessoa.getMatricula().isEmpty())) {
                    throw new RegraDeNegocioException("Matrícula é obrigatória para alunos");
                }
                pessoa.setTipo(dto.getTipo());
                pessoaUpdated = true;
            }

            if (dto.getStatus() != null) {
                pessoa.setStatus(dto.getStatus());
                pessoaUpdated = true;
            }

            if (dto.getTelefone() != null) {
                pessoa.setTelefone(dto.getTelefone());
                pessoaUpdated = true;
            }

            if (pessoaUpdated) {
                pessoaRepository.save(pessoa);
            }

            // Atualizar endereço
            if (pessoa.getEndereco() != null) {
                Endereco endereco = pessoa.getEndereco();
                boolean enderecoUpdated = false;

                if (dto.getLogradouro() != null) {
                    endereco.setLogradouro(dto.getLogradouro());
                    enderecoUpdated = true;
                }

                if (dto.getNumero() != null) {
                    endereco.setNumero(dto.getNumero());
                    enderecoUpdated = true;
                }

                if (dto.getComplemento() != null) {
                    endereco.setComplemento(dto.getComplemento());
                    enderecoUpdated = true;
                }

                if (dto.getBairro() != null) {
                    endereco.setBairro(dto.getBairro());
                    enderecoUpdated = true;
                }

                if (dto.getCidade() != null) {
                    endereco.setCidade(dto.getCidade());
                    enderecoUpdated = true;
                }

                if (dto.getEstado() != null) {
                    endereco.setEstado(dto.getEstado());
                    enderecoUpdated = true;
                }

                if (dto.getCep() != null) {
                    endereco.setCep(dto.getCep());
                    enderecoUpdated = true;
                }

                if (enderecoUpdated) {
                    enderecoRepository.save(endereco);
                }
            }
        }

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return toResponseDTO(updatedUsuario);
    }

    @Transactional
    public void changePassword(UUID id, String novaSenha) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        
        // Verificar permissões de acesso
        checkUserAccessPermission(usuario);

        // Atualizar senha
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
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
            throw new AccessDeniedException("Usuário não autenticado");
        }
        
        // ADMIN pode acessar usuários do mesmo campus
        if (currentUser.getPapel() == PapelUsuario.ROLE_ADMIN) {
            if (currentUser.getCampus() == null || targetUser.getCampus() == null) {
                throw new AccessDeniedException("Acesso negado: Usuário ou targetUser sem campus associado");
            }
            
            if (!currentUser.getCampus().getId().equals(targetUser.getCampus().getId())) {
                throw new AccessDeniedException("Acesso negado: Você só pode acessar usuários do seu campus");
            }
            return;
        }
        
        // VIGIA e COMUM só podem acessar a si mesmos
        if (!currentUser.getId().equals(targetUser.getId())) {
            throw new SecurityException("Acesso negado: Você só pode acessar seu próprio usuário");
        }
    }
    

    private void checkCreatePermission(RegistroCompletoDTO dto) {
        // SUPER_ADMIN pode criar qualquer tipo de usuário
        if (SecurityUtils.isSuperAdmin()) {
            return;
        }
        
        Usuario currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException("Usuário não autenticado");
        }
        
        // ADMIN só pode criar usuários do mesmo campus
        if (currentUser.getPapel() == PapelUsuario.ROLE_ADMIN) {
            if (currentUser.getCampus() == null) {
                throw new AccessDeniedException("Acesso negado: ADMIN sem campus associado");
            }
            
            if (dto.getCampusId() == null || !dto.getCampusId().equals(currentUser.getCampus().getId())) {
                throw new AccessDeniedException("Acesso negado: ADMIN só pode criar usuários no seu campus");
            }
            
            // ADMIN não pode criar SUPER_ADMIN
            if (dto.getPapel() == PapelUsuario.ROLE_SUPER_ADMIN) {
                throw new AccessDeniedException("Acesso negado: ADMIN não pode criar usuários com papel de SUPER_ADMIN");
            }
            return;
        }
        
        // VIGIA e COMUM não podem criar usuários
        throw new AccessDeniedException("Acesso negado: Você não tem permissão para criar usuários");
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

            if (usuario.getPessoa().getEndereco() != null) {
                dto.setEndereco(new com.luis.ifpark.dtos.endereco.EnderecoDTO(
                        usuario.getPessoa().getEndereco()
                ));
            }
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
