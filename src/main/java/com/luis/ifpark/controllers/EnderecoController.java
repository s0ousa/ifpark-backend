package com.luis.ifpark.controllers;

import com.luis.ifpark.dtos.endereco.EnderecoDTO;
import com.luis.ifpark.dtos.endereco.EnderecoUpdateDTO;
import com.luis.ifpark.services.EnderecoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/enderecos")
public class EnderecoController {
    
    @Autowired
    private EnderecoService service;

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIGIA', 'SUPER_ADMIN', 'COMUM')")
    public ResponseEntity<EnderecoDTO> findById(@PathVariable UUID id) {
        EnderecoDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIGIA', 'SUPER_ADMIN', 'COMUM')")
    public ResponseEntity<EnderecoDTO> update(@PathVariable UUID id, @Valid @RequestBody EnderecoUpdateDTO dto) {
        EnderecoDTO result = service.update(id, dto);
        return ResponseEntity.ok(result);
    }
}
