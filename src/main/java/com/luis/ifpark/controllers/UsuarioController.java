package com.luis.ifpark.controllers;

import com.luis.ifpark.dtos.auth.RegistroCompletoDTO;
import com.luis.ifpark.dtos.usuario.UsuarioCreateDTO;
import com.luis.ifpark.dtos.usuario.UsuarioResponseDTO;
import com.luis.ifpark.dtos.usuario.UsuarioUpdateDTO;
import com.luis.ifpark.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = "/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VIGIA', 'SUPER_ADMIN')")
    public ResponseEntity<Page<UsuarioResponseDTO>> findAll(
            @RequestParam(required = false) String papel,
            @PageableDefault(page = 0, size = 10, sort = "pessoa.nome", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<UsuarioResponseDTO> page = usuarioService.findAll(papel, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','VIGIA','SUPER_ADMIN', 'COMUM')")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable UUID id) {
        UsuarioResponseDTO dto = usuarioService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIGIA', 'SUPER_ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> findByEmail(@PathVariable String email) {
        UsuarioResponseDTO dto = usuarioService.findByEmail(email);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody RegistroCompletoDTO dto) {
        UsuarioResponseDTO createdDto = usuarioService.create(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdDto);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody UsuarioUpdateDTO dto) {
        UsuarioResponseDTO updatedDto = usuarioService.update(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
