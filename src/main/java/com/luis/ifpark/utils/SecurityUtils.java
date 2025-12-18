package com.luis.ifpark.utils;

import com.luis.ifpark.entities.Usuario;
import com.luis.ifpark.entities.enums.PapelUsuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {

    /**
     * Verifica se o usuário autenticado tem permissão para acessar recursos do campus especificado
     * 
     * @param campusId ID do campus a ser verificado
     * @return true se o usuário tem permissão, false caso contrário
     */
    public static boolean hasAccessToCampus(UUID campusId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        // Extrair o usuário do contexto de segurança
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Usuario)) {
            return false;
        }
        
        Usuario usuario = (Usuario) principal;
        
        // SUPER_ADMIN tem acesso a tudo
        if (usuario.getPapel() == PapelUsuario.ROLE_SUPER_ADMIN) {
            return true;
        }
        
        // Se o usuário não tem campus associado, não tem acesso
        if (usuario.getCampus() == null) {
            return false;
        }
        
        // Verificar se o usuário pertence ao campus especificado
        return usuario.getCampus().getId().equals(campusId);
    }
    
    /**
     * Verifica se o usuário autenticado é SUPER_ADMIN
     * 
     * @return true se o usuário é SUPER_ADMIN, false caso contrário
     */
    public static boolean isSuperAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        // Extrair o usuário do contexto de segurança
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Usuario)) {
            return false;
        }
        
        Usuario usuario = (Usuario) principal;
        return usuario.getPapel() == PapelUsuario.ROLE_SUPER_ADMIN;
    }
    
    /**
     * Obtém o usuário autenticado atual
     * 
     * @return Usuário autenticado ou null se não houver
     */
    public static Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof Usuario) {
            return (Usuario) principal;
        }
        
        return null;
    }
}
