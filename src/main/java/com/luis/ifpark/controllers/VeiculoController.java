package com.luis.ifpark.controllers;

import com.luis.ifpark.dtos.veiculo.VeiculoCreateDTO;
import com.luis.ifpark.dtos.veiculo.VeiculoDTO;
import com.luis.ifpark.dtos.veiculo.VeiculoRejectedDTO;
import com.luis.ifpark.services.VeiculoService;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VIGIA', 'SUPER_ADMIN')")
    public ResponseEntity<Page<VeiculoDTO>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "modelo", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<VeiculoDTO> page = veiculoService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIGIA', 'SUPER_ADMIN')")
    public ResponseEntity<VeiculoDTO> findById(@PathVariable UUID id) {
        VeiculoDTO dto = veiculoService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/campus/{campusId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIGIA', 'SUPER_ADMIN')")
    public ResponseEntity<Page<VeiculoDTO>> findByCampus(
            @PathVariable UUID campusId,
            @PageableDefault(page = 0, size = 10, sort = "modelo", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<VeiculoDTO> page = veiculoService.findByCampus(campusId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping(value = "/pessoa/{pessoaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIGIA', 'SUPER_ADMIN', 'COMUM')")
    public ResponseEntity<Page<VeiculoDTO>> findByPessoaId(
            @PathVariable UUID pessoaId,
            @PageableDefault(page = 0, size = 10, sort = "modelo", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<VeiculoDTO> page = veiculoService.findByPessoaId(pessoaId, pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    public ResponseEntity<VeiculoDTO> insert(@Valid @RequestBody VeiculoCreateDTO dto) {
        VeiculoDTO createdDto = veiculoService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(createdDto);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<VeiculoDTO> update(@PathVariable UUID id, @Valid @RequestBody VeiculoDTO dto) {
        VeiculoDTO updatedDto = veiculoService.update(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    @PutMapping(value = "/aprovar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<VeiculoDTO> aprovarVeiculo(@PathVariable UUID id) {
        VeiculoDTO updatedDto = veiculoService.aprovarVeiculo(id);
        return ResponseEntity.ok(updatedDto);
    }

    @PutMapping(value = "/rejeitar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<VeiculoDTO> rejeitarVeiculo(@PathVariable UUID id, @RequestBody VeiculoRejectedDTO dto) {
        VeiculoDTO updatedDto = veiculoService.rejeitarVeiculo(id, dto.getMotivo());
        return ResponseEntity.ok(updatedDto);
    }

    @GetMapping(value = "/placa/{placa}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIGIA', 'SUPER_ADMIN')")
    public ResponseEntity<VeiculoDTO> findByPlaca(@PathVariable String placa) {
        VeiculoDTO dto = veiculoService.findByPlaca(placa);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        veiculoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
