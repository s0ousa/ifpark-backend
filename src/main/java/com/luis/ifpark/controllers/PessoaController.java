package com.luis.ifpark.controllers;

import com.luis.ifpark.dtos.pessoa.PessoaCreateDTO;
import com.luis.ifpark.dtos.pessoa.PessoaResponseDTO;
import com.luis.ifpark.dtos.pessoa.PessoaUpdateDTO;
import com.luis.ifpark.dtos.pessoa.StatusPessoaUpdateDTO;
import com.luis.ifpark.services.PessoaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/pessoas")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VIGIA', 'SUPER_ADMIN')")
    public ResponseEntity<List<PessoaResponseDTO>> findAll() {
        List<PessoaResponseDTO> list = pessoaService.findAll();
        return ResponseEntity.ok(list);
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
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PessoaResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody PessoaUpdateDTO dto) {
        PessoaResponseDTO updatedDto = pessoaService.update(id, dto);
        return ResponseEntity.ok(updatedDto);
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
