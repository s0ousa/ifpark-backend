package com.luis.ifpark.controllers;

import com.luis.ifpark.dtos.campus.CampusCreateDTO;
import com.luis.ifpark.dtos.campus.CampusDTO;
import com.luis.ifpark.dtos.campus.CampusUpdateDTO;
import com.luis.ifpark.services.CampusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = "/campus")
public class CampusController {
    
    @Autowired
    private CampusService service;

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIGIA','COMUM','SUPER_ADMIN')")
    public ResponseEntity<CampusDTO> findById(@PathVariable UUID id) {
        CampusDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<CampusDTO>> findAll(Pageable pageable) {
        Page<CampusDTO> dtoPage = service.findAll(pageable);
        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CampusDTO> insert(@Valid @RequestBody CampusCreateDTO dto) {
        CampusDTO campusDTO = service.insert(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(campusDTO.getId())
                .toUri();
        return ResponseEntity.created(uri).body(campusDTO);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CampusDTO> update(@PathVariable UUID id, @Valid @RequestBody CampusUpdateDTO dto) {
        CampusDTO result = service.update(id, dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
