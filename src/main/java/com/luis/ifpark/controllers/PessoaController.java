package com.luis.ifpark.controllers;

import com.luis.ifpark.dtos.pessoa.*;
import com.luis.ifpark.services.PessoaService;
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
@RequestMapping(value = "/pessoas")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VIGIA', 'SUPER_ADMIN')")
    public ResponseEntity<Page<PessoaResponseDTO>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<PessoaResponseDTO> page = pessoaService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIGIA', 'SUPER_ADMIN')")
    public ResponseEntity<PessoaResponseDTO> findById(@PathVariable UUID id) {
        PessoaResponseDTO dto = pessoaService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/cpf/{cpf}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIGIA', 'SUPER_ADMIN')")
    public ResponseEntity<PessoaResponseDTO> findByCpf(@PathVariable String cpf) {
        PessoaResponseDTO dto = pessoaService.findByCpf(cpf);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/visitantes")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIGIA', 'SUPER_ADMIN')")
    public ResponseEntity<PessoaResponseDTO> createVisitor(@Valid @RequestBody VisitanteDTO dto) {
        PessoaResponseDTO createdDto = pessoaService.createVisitor(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdDto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PessoaResponseDTO> create(@Valid @RequestBody PessoaCreateDTO dto) {
        PessoaResponseDTO createdDto = pessoaService.create(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdDto);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','VIGIA','SUPER_ADMIN')")
    public ResponseEntity<PessoaResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody PessoaUpdateDTO dto) {
        PessoaResponseDTO updatedDto = pessoaService.update(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    @GetMapping("/motoristas")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'VIGIA')")
    public ResponseEntity<Page<MotoristaResponseDTO>> listar(
            @PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<MotoristaResponseDTO> resultado = pessoaService.findAllDrivers(pageable);
        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        pessoaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PessoaResponseDTO> atualizarStatus(
            @PathVariable UUID id,
            @RequestBody @Valid StatusPessoaUpdateDTO dto) {

        PessoaResponseDTO pessoaAtualizada = pessoaService.atualizarStatus(id, dto.getStatus());
        return ResponseEntity.ok(pessoaAtualizada);
    }
}
