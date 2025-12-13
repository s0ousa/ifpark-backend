package com.luis.ifpark.controllers;

import com.luis.ifpark.dtos.EstacionamentoDTO;
import com.luis.ifpark.dtos.estacionamento.EstacionamentoCreateDTO;
import com.luis.ifpark.dtos.estacionamento.EstacionamentoUpdateDTO;
import com.luis.ifpark.services.EstacionamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = "/estacionamentos")
public class EstacionamentoController {
    
    @Autowired
    private EstacionamentoService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<EstacionamentoDTO> findById(@PathVariable UUID id) {
        EstacionamentoDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<EstacionamentoDTO>> findAll(Pageable pageable) {
        Page<EstacionamentoDTO> dtoPage = service.findAll(pageable);
        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping
    public ResponseEntity<EstacionamentoDTO> insert(@Valid @RequestBody EstacionamentoCreateDTO dto) {
        EstacionamentoDTO estacionamentoDTO = service.insert(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(estacionamentoDTO.getId())
                .toUri();
        return ResponseEntity.created(uri).body(estacionamentoDTO);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<EstacionamentoDTO> update(@PathVariable UUID id, @Valid @RequestBody EstacionamentoUpdateDTO dto) {
        EstacionamentoDTO result = service.update(id, dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
