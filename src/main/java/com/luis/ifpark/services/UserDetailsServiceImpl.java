package com.luis.ifpark.services;

import com.luis.ifpark.entities.Usuario;
import com.luis.ifpark.entities.enums.StatusPessoa;
import com.luis.ifpark.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + username));

        // Validação do status da pessoa
        if (usuario.getPessoa() == null) {
            System.out.println("ERRO: Usuario sem pessoa vinculada");
            throw new DisabledException("Usuário sem pessoa vinculada.");
        }

        StatusPessoa status = usuario.getPessoa().getStatus();

        if (status != StatusPessoa.ATIVO) {
            System.out.println("LANÇANDO DISABLED EXCEPTION - Status: " + status);

            String mensagem;
            switch (status) {
                case SUSPENSO:
                    mensagem = "Sua conta foi suspensa. Entre em contato com o suporte.";
                    break;
                case INATIVO:
                    mensagem = "Conta inativa. Por favor, reative sua conta.";
                    break;
                case PENDENTE:
                    mensagem = "Sua conta ainda está pendente de aprovação.";
                    break;
                default:
                    mensagem = "Conta não autorizada para login.";
            }

            System.out.println("Mensagem: " + mensagem);
            throw new DisabledException(mensagem);
        }
        return usuario;
    }
}