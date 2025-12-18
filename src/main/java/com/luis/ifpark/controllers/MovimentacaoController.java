package com.luis.ifpark.controllers;

import com.luis.ifpark.dtos.MovimentacaoDTO;
import com.luis.ifpark.entities.Usuario;
import com.luis.ifpark.repositories.UsuarioRepository;
import com.luis.ifpark.security.JwtUtil;
import com.luis.ifpark.services.MovimentacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/movimentacoes")
public class MovimentacaoController {

    @Autowired
    private MovimentacaoService movimentacaoService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('VIGIA', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<MovimentacaoDTO>> findAll(Pageable pageable) {
        Page<MovimentacaoDTO> dtoPage = movimentacaoService.findAll(pageable);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('VIGIA', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MovimentacaoDTO> findById(@PathVariable UUID id) {
        MovimentacaoDTO dto = movimentacaoService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/estacionamento/{estacionamentoId}")
    @PreAuthorize("hasAnyRole('VIGIA', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<MovimentacaoDTO>> findVeiculosNoEstacionamento(@PathVariable UUID estacionamentoId) {
        List<MovimentacaoDTO> list = movimentacaoService.findVeiculosNoEstacionamento(estacionamentoId);
        return ResponseEntity.ok(list);
    }

    @PostMapping(value = "/entrada")
    @PreAuthorize("hasAnyRole('VIGIA', 'SUPER_ADMIN')")
    public ResponseEntity<MovimentacaoDTO> registrarEntrada(@Valid @RequestBody MovimentacaoDTO dto, 
                                                           @RequestHeader("Authorization") String authHeader) {
        UUID vigiaId = extractUserIdFromToken(authHeader); // Este método precisa ser implementado
        MovimentacaoDTO createdDto = movimentacaoService.registrarEntrada(dto, vigiaId);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdDto);
    }

    @PostMapping(value = "/saida/{id}")
    @PreAuthorize("hasAnyRole('VIGIA', 'SUPER_ADMIN')")
    public ResponseEntity<MovimentacaoDTO> registrarSaida(@PathVariable UUID id, 
                                                         @RequestHeader("Authorization") String authHeader) {
        UUID vigiaId = extractUserIdFromToken(authHeader);
        MovimentacaoDTO updatedDto = movimentacaoService.registrarSaida(id, vigiaId);
        return ResponseEntity.ok(updatedDto);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MovimentacaoDTO> update(@PathVariable UUID id, @Valid @RequestBody MovimentacaoDTO dto) {
        MovimentacaoDTO updatedDto = movimentacaoService.update(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        movimentacaoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Método auxiliar para extrair o ID do usuário do token JWT
    private UUID extractUserIdFromToken(String authHeader) {
        // Remover o prefixo "Bearer " do header
        String token = authHeader.substring(7);
        
        // Extrair o email do token usando JwtUtil
        String email = jwtUtil.getUsernameFromToken(token);
        
        // Buscar o usuário pelo email e retornar seu ID
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com email: " + email));
            
        return usuario.getId();
    }
}
